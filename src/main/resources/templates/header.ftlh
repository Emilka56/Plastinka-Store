<link rel="stylesheet" href="/styles/header.css">
<link rel="stylesheet" href="/styles/font.css">

<header>
    <nav>
        <ul class="nav">
            <#if user??>
            <li><a class="nav-button" href="/profile">МОЙ ПРОФИЛЬ</a></li>
            </#if>
            <li><a class="nav-button" href="/">ГЛАВНАЯ СТРАНИЦА</a></li>
            <li class="genre-dropdown">
                <a class="nav-button genre-btn" href="#">ЖАНР</a>
                <select class="genre-select" onchange="if (this.value) { window.location.href=this.value; this.classList.remove('active'); document.querySelector('.genre-btn').style.display = 'inline-block'; }">
                    <option value="" selected disabled>Выберите жанр</option>
                    <#if genres??>
                        <#list genres as genre>
                            <option value="#${genre}">${genre}</option>
                        </#list>
                    </#if>
                </select>
            </li>
            <li>
                <a class="nav-button" href="/cart">КОРЗИНА<span id="cart-item-count"></span></a>
            </li>
            <#if user?? && user.role == 'ADMIN'>
                <li><a class="nav-button" href="/adminPage">АДМИН</a></li>
            </#if>
            <#if user??>
                <li><a class="nav-button" href="/logout">ВЫЙТИ</a></li>
            <#else>
                <li><a class="nav-button" href="/login">ВОЙТИ</a></li>
            </#if>
        </ul>
    </nav>
</header>

<script>
document.querySelector('.genre-btn').addEventListener('click', function(e) {
    e.preventDefault();
    this.style.display = 'none';
    const select = document.querySelector('.genre-select');
    select.classList.add('active');
    select.focus();
});

document.querySelector('.genre-select').addEventListener('blur', function() {
    this.classList.remove('active');
    document.querySelector('.genre-btn').style.display = 'inline-block';
});

// Обновляем количество товаров при загрузке страницы
updateCartQuantity();
</script>