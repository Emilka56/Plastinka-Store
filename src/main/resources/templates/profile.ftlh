<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Профиль пользователя</title>
    <link rel="stylesheet" href="/styles/global.css">
    <link rel="stylesheet" href="/styles/profile.css">
    <link rel="stylesheet" href="/styles/font.css">
</head>
<body>
    <#include "header.ftlh">
    <input type="hidden" id="csrfToken" name="${_csrf.parameterName}" value="${_csrf.token}">
    
    <main class="profile-container">
        <section class="user-info">
            <h2>Личные данные</h2>
            <div class="info-card">
                <div class="info-row">
                    <span class="label">Имя:</span>
                    <span class="value">${user.firstName}</span>
                </div>
                <div class="info-row">
                    <span class="label">Фамилия:</span>
                    <span class="value">${user.lastName}</span>
                </div>
                <div class="info-row">
                    <span class="label">Email:</span>
                    <span class="value">${user.email}</span>
                </div>
            </div>
        </section>

        <section class="orders-section">
            <h2>История заказов</h2>
            <#if orders?? && orders?has_content>
                <div class="orders-list">
                    <#list orders as order>
                        <div class="order-card">
                            <div class="order-header">
                                <span class="order-date">Оформлен: ${order.formattedCreatedAt}</span>
                            </div>
                            
                            <div class="order-items">
                                <#list order.singleOrders as item>
                                    <div class="order-item">
                                        <div class="item-image">
                                            <#if item.product.images?? && item.product.images?has_content>
                                                <img src="/files/${item.product.images[0].storageName}" 
                                                     alt="${item.product.album}" 
                                                     class="product-image">
                                            <#else>
                                                <img src="/images/default-vinyl.jpg" 
                                                     alt="${item.product.album}" 
                                                     class="product-image">
                                            </#if>
                                        </div>
                                        <div class="item-details">
                                            <h3>${item.product.artist} - ${item.product.album}</h3>
                                            <p class="item-quantity">Количество: ${item.quantity}</p>
                                            <p class="item-price">Цена: ${item.product.price} руб</p>
                                        </div>
                                    </div>
                                </#list>
                            </div>
                            
                            <div class="order-footer">
                                <div class="total-price">
                                    Итого: ${order.totalPrice} руб
                                </div>
                                <#if order.address??>
                                    <div class="delivery-address">
                                        Адрес доставки: ${order.address.street}, ${order.address.house}
                                        <#if order.address.apartment??>, кв. ${order.address.apartment}</#if>
                                    </div>
                                </#if>
                            </div>
                        </div>
                    </#list>
                </div>
            <#else>
                <p class="no-orders">У вас пока нет заказов</p>
            </#if>
        </section>
    </main>
</body>
</html> 