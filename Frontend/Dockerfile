FROM node:22.23.3-alpine3.24 AS build-stage

WORKDIR /app
RUN corepack enable && corepack prepare pnpm@10.27.0 --activate
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile
COPY . .
RUN pnpm run build

FROM nginx:1.31-alpine3.24 AS production-stage

COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY ./nginx/default.conf /etc/nginx/conf.d/default.conf
COPY ./nginx/substitute_environment_variables_in_manifest.sh /docker-entrypoint.d/substitute_environment_variables_in_manifest.sh
RUN chmod +x /docker-entrypoint.d/substitute_environment_variables_in_manifest.sh

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]
