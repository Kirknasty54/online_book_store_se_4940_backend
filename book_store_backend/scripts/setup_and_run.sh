#!/bin/bash
# Setup script to create virtual environment and run the book population script

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
VENV_DIR="$SCRIPT_DIR/.venv"

echo "🔧 Setting up Python virtual environment..."

# Create virtual environment if it doesn't exist
if [ ! -d "$VENV_DIR" ]; then
    echo "Creating new virtual environment..."
    python3 -m venv "$VENV_DIR"
else
    echo "Virtual environment already exists."
fi

# Activate virtual environment
echo "Activating virtual environment..."
source "$VENV_DIR/bin/activate"

# Upgrade pip
echo "Upgrading pip..."
pip install --upgrade pip

# Install requirements
echo "Installing dependencies..."
pip install -r "$SCRIPT_DIR/requirements.txt"

# Run the script
echo ""
echo "🚀 Running book population script..."
python "$SCRIPT_DIR/populate_books.py"

# Deactivate virtual environment
deactivate

echo ""
echo "✅ Done!"
