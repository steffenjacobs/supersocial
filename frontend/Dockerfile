FROM node:13.11.0-alpine as build
WORKDIR /app/
COPY src src
COPY public public
COPY package.json package.json
COPY tsconfig.json tsconfig.json
RUN npm install
RUN npm run build

FROM nginx:1.16.1-alpine
RUN rm -rf /etc/nginx/conf.d
COPY nginx_conf/conf /etc/nginx
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
