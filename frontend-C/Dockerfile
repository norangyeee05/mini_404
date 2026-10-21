FROM node:22-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci
COPY . .

# Vite embeds VITE_* values into the browser bundle during this build.
ARG VITE_KAKAO_MAP_KEY
ENV VITE_KAKAO_MAP_KEY=${VITE_KAKAO_MAP_KEY}
RUN npm run build

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
