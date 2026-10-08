FROM nginx:1.30.5-alpine3.24

COPY ./nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 7000

CMD ["nginx", "-g", "daemon off;"]
