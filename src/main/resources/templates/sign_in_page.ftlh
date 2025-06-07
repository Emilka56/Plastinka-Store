<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/styles/global.css">
    <link rel="stylesheet" href="/styles/signIn.css">
    <link rel="stylesheet" href="/styles/font.css">
    <title>Вход</title>
</head>
<body>
    <div class="container">
        <form class="sign-in-form" action="/signIn" method="post">
            <img class="logo" src="/images/plastinka.png" alt="Logo">
            <h2>Вход</h2>
            
            <div class="form-group">
                <input name="email" type="email" placeholder="Email" required>
            </div>
            
            <div class="form-group">
                <input name="password" type="password" placeholder="Пароль" required>
            </div>
            
            <input type="submit" value="ВОЙТИ">
            
            <div class="form-footer">
                <p>Нет аккаунта? <a href="/signUp" class="signUp">Зарегистрируйтесь</a></p>
            </div>
        </form>
    </div>
</body>
</html>