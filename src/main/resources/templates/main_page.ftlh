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
    <script src="/js/cart.js" defer></script>
    <script src="/js/products.js" defer></script>
    <title>Plastinka</title>
</head>
<body>
<#include "header.ftlh">
<input type="hidden" id="csrfToken" name="${_csrf.parameterName}" value="${_csrf.token}">
<main>
    <div class="main-image-container">
        <img src="/images/mainPagePicture.jpg" class="main-image" alt="Main"/>
    </div>
    <h1>Plastinka</h1>
    
    <#if genres?? && genres?has_content>
    <#list genres as genre>
        <section id="genre-${genre?counter}">
            <h3 class="name-genre">${genre}</h3>
            <div class="product-container">
                <#if products?? && products?has_content>
                <#list products as product>
                    <#if product.genre?? && product.genre == genre>
                        <div class="product-card" onclick="window.location.href='/product/${product.id}'">
                            <#if product.images?? && product.images?has_content>
                                <img src="/files/${product.images[0].storageName}" alt="${product.album}" class="product-img">
                            <#else>
                                <img src="/images/default-vinyl.jpg" alt="${product.album}" class="product-img">
                            </#if>
                            <p class="artist">${product.artist}</p>
                            <p class="album">${product.album}</p>
                            <p class="price">${product.price} руб</p>
                            <button class="add-to-cart-btn" onclick="event.stopPropagation(); addToCart(${product.id})">
                                В корзину
                            </button>
                        </div>
                    </#if>
                </#list>
                </#if>
            </div>
            <div class="load-more-container">
                <button class="load-more-btn" onclick="loadMoreProducts('${genre}', 'genre-${genre?counter}')">
                    Загрузить ещё пластинки
                </button>
            </div>
        </section>
    </#list>
    </#if>

    <div class="loading-spinner" id="loadingSpinner"></div>
</main>

<script>
    function showLoading() {
        document.getElementById('loadingSpinner').style.display = 'block';
    }

    function hideLoading() {
        document.getElementById('loadingSpinner').style.display = 'none';
    }

    function addNewGenre() {
        const newGenre = document.getElementById('newGenreInput').value.trim();
        if (!newGenre) {
            alert('Введите название жанра');
            return;
        }
        
        showLoading();
        const csrfToken = document.getElementById('csrfToken').value;
        
        fetch('/mainPage/add-genre', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-CSRF-TOKEN': csrfToken
            },
            body: 'genre=' + encodeURIComponent(newGenre)
        })
        .then(response => response.json())
        .then(data => {
            hideLoading();
            if (data.error) {
                alert(data.error);
            } else {
                // Перезагружаем страницу для отображения нового жанра
                window.location.reload();
            }
        })
        .catch(error => {
            hideLoading();
            console.error('Ошибка:', error);
            alert('Произошла ошибка при добавлении жанра');
        });
    }
</script>
</body>
</html>
