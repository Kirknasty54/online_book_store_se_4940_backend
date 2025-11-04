#!/usr/bin/env python3
"""
Script to fetch books from Google Books API and populate the database.
"""
import os
import sys
import requests
import psycopg
from dotenv import load_dotenv
import random

# Load environment variables
load_dotenv('../src/main/resources/.env')

# Database connection configuration
DB_CONFIG = {
    'host': 'bookstore-db.c986wiuwap7e.us-east-1.rds.amazonaws.com',
    'port': 5432,
    'dbname': 'postgres',
    'user': os.getenv('DATABASE_USERNAME', 'postgres'),
    'password': os.getenv('DATABASE_PASSWORD')
}

# Google Books API configuration
GOOGLE_BOOKS_API = "https://www.googleapis.com/books/v1/volumes"
SEARCH_QUERIES = [
    "fiction", "science fiction", "mystery", "thriller", "romance",
    "fantasy", "historical fiction", "biography", "self-help", "business"
]


def fetch_books_from_api(query, max_results=40):
    """Fetch books from Google Books API."""
    params = {
        'q': query,
        'maxResults': max_results,
        'printType': 'books',
        'langRestrict': 'en'
    }

    try:
        response = requests.get(GOOGLE_BOOKS_API, params=params, timeout=10)
        response.raise_for_status()
        return response.json().get('items', [])
    except requests.exceptions.RequestException as e:
        print(f"Error fetching books for query '{query}': {e}")
        return []


def extract_isbn(identifiers):
    """Extract ISBN from industry identifiers."""
    if not identifiers:
        return None

    # Prefer ISBN_13 over ISBN_10
    for identifier in identifiers:
        if identifier.get('type') == 'ISBN_13':
            return int(identifier.get('identifier'))

    for identifier in identifiers:
        if identifier.get('type') == 'ISBN_10':
            isbn_10 = identifier.get('identifier')
            # Convert ISBN-10 to a numeric value
            return int(isbn_10.replace('-', '').replace('X', '0'))

    return None


def transform_book_data(book_item):
    """Transform Google Books API response to match database schema."""
    volume_info = book_item.get('volumeInfo', {})

    # Extract ISBN
    isbn = extract_isbn(volume_info.get('industryIdentifiers'))
    if not isbn:
        return None

    # Extract other fields with defaults
    title = volume_info.get('title', 'Unknown Title')
    authors = volume_info.get('authors', ['Unknown Author'])
    author = ', '.join(authors)
    publisher = volume_info.get('publisher', 'Unknown Publisher')
    published_date = volume_info.get('publishedDate', '2024-01-01')
    description = volume_info.get('description', 'No description available')

    # Get image URLs in different sizes
    # Google Books API provides: smallThumbnail, thumbnail, small, medium, large, extraLarge
    image_links = volume_info.get('imageLinks', {})
    placeholder = 'https://via.placeholder.com/150'

    # Map to small, medium, large
    image_url_small = image_links.get('small', image_links.get('smallThumbnail', placeholder))
    image_url_medium = image_links.get('medium', image_links.get('thumbnail', placeholder))
    image_url_large = image_links.get('large',
                                     image_links.get('extraLarge',
                                                    image_links.get('medium', placeholder)))

    # Generate random price and stock
    price = round(random.uniform(9.99, 49.99), 2)
    stock = random.randint(0, 100)

    return {
        'isbn_id': isbn,
        'title': title[:250],
        'author': author[:250],
        'publisher': publisher[:250],
        'published_date': published_date[:250],
        'description': description[:2000],  # TEXT column can handle more
        'price': price,
        'stock': stock,
        'image_url_small': image_url_small[:500],
        'image_url_medium': image_url_medium[:500],
        'image_url_large': image_url_large[:500]
    }


def insert_books_to_db(books):
    """Insert books into the database."""
    if not books:
        print("No books to insert.")
        return 0

    conn = None
    try:
        conn = psycopg.connect(**DB_CONFIG)
        cursor = conn.cursor()

        # Prepare insert query with ON CONFLICT to handle duplicates
        insert_query = """
            INSERT INTO books (isbn_id, title, author, publisher, published_date,
                             description, price, stock, image_url_small, image_url_medium, image_url_large)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            ON CONFLICT (isbn_id) DO NOTHING
        """

        # Execute batch insert
        inserted_count = 0
        for book in books:
            cursor.execute(insert_query, (
                book['isbn_id'],
                book['title'],
                book['author'],
                book['publisher'],
                book['published_date'],
                book['description'],
                book['price'],
                book['stock'],
                book['image_url_small'],
                book['image_url_medium'],
                book['image_url_large']
            ))
            if cursor.rowcount > 0:
                inserted_count += 1

        conn.commit()
        print(f"Successfully inserted {inserted_count} books into the database.")

        cursor.close()
        return inserted_count

    except psycopg.Error as e:
        print(f"Database error: {e}")
        if conn:
            conn.rollback()
        return 0
    finally:
        if conn:
            conn.close()


def main():
    """Main function to orchestrate the book population process."""
    print("Starting book population script...")
    print(f"Connecting to database at {DB_CONFIG['host']}...")

    all_books = []

    for query in SEARCH_QUERIES:
        print(f"\nFetching books for query: '{query}'")
        book_items = fetch_books_from_api(query)
        print(f"Found {len(book_items)} books")

        for item in book_items:
            book_data = transform_book_data(item)
            if book_data:
                all_books.append(book_data)

    # Remove duplicates based on ISBN
    unique_books = {book['isbn_id']: book for book in all_books}.values()
    print(f"\nTotal unique books collected: {len(unique_books)}")

    # Insert into database
    inserted = insert_books_to_db(list(unique_books))
    print(f"\n✓ Process complete! {inserted} new books added to the database.")


if __name__ == "__main__":
    main()
