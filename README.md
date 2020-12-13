# Divao Passwordless

Divao Passwordless Sign-On allows you to sign-up and sign-in users without any passwords.

## How Does it work?

Divao Passworldess helps service providers verify their user's digital identity (e.g. email address, phone number).

1. User provides the digital identity (e.g. `john@example.org`)
1. Divao sends a message with a secret link (e.g. `https://divao.io/x2IOrGDkLNVZsDYptNXaAg`)
1. User clicks on the secret link, thereby proving that they own that digital identity
1. Divao issues a standardized [JSON Web Token (JWT)](https://en.wikipedia.org/wiki/JSON_Web_Token) that can be verified on the backend 

## Getting started

### How to add Divao Passwordless to your web app

With these four simple steps you can add Divao Passwordless to your web app.

#### 1. Include JavaScript

Inlcude the Divao library by adding the following line of code to your website:

```html
<script async data-api-key="YOUR_DIVAO_API_KEY" src="https://api.divao.io/v1/"></script>
```

#### 2. Show sign dialog

Show the Divao sign dialog:

```html
<button onclick="Divao.sign()">Sign without password</button>
```

#### 3. React to the sign-in

Retrieve the token:

```js
window.addEventListener("DivaoSignOn", () =>
{
    if(Divao.JWT !== null)
    {
        console.log(Divao.JWT.payload.sub); //e.g. alice@example.org
        console.log(Divao.JWT.toString()) //e.g. eyJhbGciO...
        const header = `Bearer ${Divao.JWT}`;    
        //use to call backend API
    }
    else
    {
        console.log("not signed in");
    }
}
```

#### 4. Verify token

Verify the token on your backend:

```js
import {JWT} from "jose";

const payload = JWT.verify(jwt, DIVAO_PUBLIC_KEY, {aud: "acme.example.com"});
if(payload !== null)
{
    //user is logged in
    console.log(payload.sub); //e.g. john@example.org
}
```