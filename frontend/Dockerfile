# ---- Build stage: build the Vite app ----
FROM node:22-alpine AS build
WORKDIR /app

# Install dependencies first (cached as long as package*.json do not change)
COPY package.json package-lock.json ./
RUN npm ci

# Call the API on the same origin as the page (/api/...): nginx forwards it to the back-end
ENV VITE_API_URL=/
COPY . .
RUN npm run build

# ---- Runtime stage: nginx serves the static files ----
FROM nginx:stable-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
