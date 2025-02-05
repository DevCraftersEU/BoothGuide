FROM nginx:stable-alpine
# Copy the nginx configuration file
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
# Expose the port 7000
EXPOSE 7000
# Start Nginx to serve the application
CMD ["nginx", "-g", "daemon off;"]
