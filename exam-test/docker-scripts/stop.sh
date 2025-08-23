#!/bin/bash

echo "=== Stopping MySQL Database ==="

# Stop và remove containers
echo "🛑 Stopping MySQL service..."
docker-compose down

# Remove volumes (optional - uncomment if you want to remove data)
# echo "🗑️ Removing volumes..."
# docker-compose down -v

# Remove images (optional - uncomment if you want to remove images)
# echo "🗑️ Removing images..."
# docker-compose down --rmi all

echo "✅ MySQL stopped successfully!"
echo ""
echo "💡 To start MySQL again, run: ./docker-scripts/start.sh" 