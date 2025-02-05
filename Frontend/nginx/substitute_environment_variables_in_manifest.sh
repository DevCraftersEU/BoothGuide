#!/bin/sh
# Replace env vars in files served by NGINX
echo "Replacing \$PWA_TITLE_SHORT with $PWA_TITLE_SHORT"
sed -i "s|\$PWA_TITLE_SHORT|$PWA_TITLE_SHORT|g" /usr/share/nginx/html/manifest.webmanifest
echo "Replacing \$PWA_TITLE with $PWA_TITLE"
sed -i "s|\$PWA_TITLE|$PWA_TITLE|g" /usr/share/nginx/html/manifest.webmanifest
exec "$@"
