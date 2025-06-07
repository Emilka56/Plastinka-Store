<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="stylesheet" href="/styles/global.css">
    <link rel="stylesheet" href="/styles/signUp.css">
    <link rel="stylesheet" href="/styles/font.css">

    <link rel="preload" href="/fonts/Roboto-Regular.ttf" as="font">
    <link rel="preload" href="/fonts/Arizonia-Regular.ttf" as="font">
    <title>Регистрация</title>

</head>
<body>
<form action="/signUp" method="post">
    <div class="form-header">
        <h2>Регистрация</h2>
    </div>

    <input name="email" type="email" placeholder="EMAIL" required>
    <input name="password" type="password" placeholder="PASSWORD" required>
    <input name="firstName" type="text" placeholder="ИМЯ" required>
    <input name="lastName" type="text" placeholder="ФАМИЛИЯ" required>

    <input type="submit" value="ЗАРЕГИСТРИРОВАТЬСЯ">

    <div class="form-footer">
        Уже есть аккаунт? <a href="/signIn">Войдите</a>
    </div>
</form>
</body>
</html>