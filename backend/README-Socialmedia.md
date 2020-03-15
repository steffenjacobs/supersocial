# Setting up for Facebook
## Getting the Facebook Page ID
1. Go to your page
2. Click on "See more" in the left column
3. Click on "About" in the left column
4. View the Page ID under "More Info"
5. Copy the Page ID into `credentials.properties` &rarr; `facebook.page.id`

## Obtaining a Facebook Page API Access Token

1. Create an app at https://developers.facebook.com/apps
2. Go to the [Graph API Explorer](https://developers.facebook.com/tools/explorer)
3. Make sure the following Permissions are selected: 
    * `manage\_pages`
    * `pages\_show\_list`
    * `publish\_pages`
    * `public\_profile`
3. Click "User Token"  &rarr;  "Get User Access Token"
4. Click "Continue as xxxx"
5. Copy the Token
6. Exchange the user token to a long-lived access token: `https://graph.facebook.com/{pageId}?access\_token={token}&fields=access\_token` or `localhost:8080/api/facebook/exchange/{token}`
7. Copy the resulting token into `credentials.properties` &rarr; `facebook.page.accesstoken`

# Obtaining a Twitter API Token
1. Go to [Twitter Developer Overview](https://developer.twitter.com/en/apps)
2. Go into your app
3. Go to "Keys and Tokens"
4. View the API key (`credentials.properties` &rarr; `twitter.api.key`
5. View the API secret key (`credentials.properties` &rarr; `twitter.api.secret`
6. Under "Access token & access token secret" click on "Regenerate" to generate a new acess token with secret
7. Copy the Access Token (`credentials.properties` &rarr; `twitter.api.accesstoken`)
8. Copy the Access Token Secret (`credentials.properties` &rarr; `twitter.api.accesstoken.secret`)
