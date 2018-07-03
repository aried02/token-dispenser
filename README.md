# token-dispenser
Stores email-password pairs, gives out Google Play Store tokens.

Using Google Play Store API requires logging in using email and password. If you have a project which works with Google Play Store API you no longer have to make the users use their live accounts or ship your software with your account credentials inside. You can deploy a token dispenser instance and it will provide auth tokens on demand without letting the world know your password.

### Building

1. `git clone https://github.com/aried02/token-dispenser`
2. `cd token-dispenser`
3. Add your user/pass to `passwords/passwords.txt`
4. `mvn install`
5. `java -jar target/token-dispenser.jar`
This will print to stdout the token + " " + gsfid for accessing the gpapi using gplaycli

Mainly envisioned as for use with token refreshing in gplaycli. My modified code is mainly just in `Token.java`, but I changed around some other files as tests too.
