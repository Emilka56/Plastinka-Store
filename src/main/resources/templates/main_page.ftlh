<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/styles/global.css">
    <link rel="stylesheet" href="/styles/mainPage.css">
    <link rel="stylesheet" href="/styles/font.css">

    <link rel="preload" href="/fonts/Roboto-Regular.ttf" as="font">
    <link rel="preload" href="/fonts/Arizonia-Regular.ttf" as="font">
    <script src="/js/cart.js"></script>
    <title>Plastinka</title>
</head>
<body>
<#include "header.ftlh">
<main>
    <div class="main-image-container">
        <img src="/images/mainPagePicture.jpg" class="main-image" alt="Main"/>
    </div>
    <h1>Plastinka</h1>
    
    <#if genres?? && genres?has_content>
    <#list genres as genre>
        <section id="${genre}">
            <h3 class="name-genre">${genre}</h3>
                <div class="product-container">
                    <#if products?? && products?has_content>
                <#list products as product>
                            <#if product.genre?? && product.genre == genre>
                                <div class="product-card" onclick="window.location.href='/product/${product.id}'">
                                    <#if product.images?? && product.images?has_content>
                                        <img src="http://localhost:8080/files/${product.images[0].storageName}" alt="${product.album}" class="product-img">
                                    </#if>
                                <p class="artist">${product.artist}</p>
                                <p class="album">${product.album}</p>
                                    <p class="price">${product.price} руб</p>
                            </div>
                        </#if>
                        </#list>
                    </#if>
            </div>
        </section>
    </#list>
    </#if>
</main>

</body>
<script>


</script>
</html>
