# Estágio 1: Build da aplicação Angular
FROM node:22-alpine AS build
WORKDIR /app

# Copia os arquivos de pacotes para otimizar o cache de build do Docker
COPY package*.json ./
RUN npm ci

# Copia o restante do código fonte do front-end
COPY . .

# Compila o projeto Angular
RUN npm run build -- --configuration=production

# Estágio 2: Servidor Web leve para servir o build
FROM nginx:alpine

# Copia a configuração do Nginx para suportar o roteamento do Angular (SPA)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copia os arquivos estáticos compilados para o diretório padrão do Nginx
COPY --from=build /app/dist/dependency_heaven/browser /usr/share/nginx/html

# Expõe a porta 80 do Nginx
EXPOSE 80

# Inicia o Nginx
CMD ["nginx", "-g", "daemon off;"]
