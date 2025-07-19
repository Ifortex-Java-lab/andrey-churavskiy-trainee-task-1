# Quick Start

1. **Expose port 8080 via ngrok**
   ```sh
   ngrok http 8080
   ```
   Copy the generated public URL.

2. **Set Stripe webhook**
    - Log in to your Stripe account (email: `andrey.churavskiy@ifortex.com`, password: `2as4Ye8*%I`).
    - Add the following webhook endpoint (replace `<ngrok-url>` with the copied address):
      ```
      <ngrok-url>/api/v1/stripe/webhook
      ```

3. **Start the application**
   ```sh
   docker-compose up --build
   ```