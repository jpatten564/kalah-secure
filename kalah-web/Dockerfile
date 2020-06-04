FROM nginx
EXPOSE 80
COPY ./web /usr/share/nginx/html

COPY ./nginx/default.conf.template /etc/nginx/conf.d/default.conf.template
COPY docker-entrypoint.sh /
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]