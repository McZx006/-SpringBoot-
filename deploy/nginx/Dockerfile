FROM node:18-alpine AS student-build
WORKDIR /workspace/student-web
COPY student-web/package*.json ./
RUN npm ci
COPY student-web/ ./
RUN npm run build

FROM node:18-alpine AS admin-build
WORKDIR /workspace/admin-web
COPY admin-web/package*.json ./
RUN npm ci
COPY admin-web/ ./
RUN VUE_APP_PUBLIC_PATH=/admin/ npm run build

FROM nginx:1.25-alpine
COPY deploy/nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=student-build /workspace/student-web/dist /usr/share/nginx/html
COPY --from=admin-build /workspace/admin-web/dist /usr/share/nginx/html/admin
EXPOSE 80
