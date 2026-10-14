# TradeNov Backend API Setup & Contract

This document details the REST API contract between the TradeNov Android mobile app and the NestJS backend service.

---

## 1. Authentication Endpoints

### `POST /api/v1/auth/login`
- **Request Body**:
  ```json
  {
    "emailOrPhone": "aman.trader@tradenov.com",
    "password": "Tradenov@123"
  }
  ```
- **Response (200 OK)**:
  ```json
  {
    "success": true,
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "user": {
        "id": "usr_001",
        "name": "Aman Verma",
        "email": "aman.trader@tradenov.com",
        "phone": "+91 98765 43210",
        "role": "TRADER",
        "expertStatus": "APPROVED",
        "walletBalance": 2850.0
      }
    }
  }
  ```

---

## 2. Tip Purchases & Social VIP Onboarding

### `POST /api/v1/purchases/signals/:id`
- **Headers**: `Authorization: Bearer <token>`
- **Response (200 OK)**:
  ```json
  {
    "success": true,
    "data": {
      "purchaseId": "TXN_782194",
      "tipId": "tip_001",
      "amountPaid": 99.0,
      "walletBalance": 2751.0,
      "vipChannels": {
        "telegram": {
          "channelName": "TradeNov Options Elite Desk",
          "inviteUrl": "https://t.me/TradeNovIndexOptions",
          "botUsername": "@tradenovbot",
          "botStartUrl": "https://t.me/tradenovbot"
        },
        "whatsapp": {
          "groupName": "TradeNov VIP Community",
          "inviteUrl": "https://chat.whatsapp.com/TradeNovVIPAlerts"
        }
      }
    }
  }
  ```

---

## 3. Social VIP Broadcast Channels

### `GET /api/v1/notifications/vip-channels?segment=OPTIONS_INDEX`
- **Response (200 OK)**:
  ```json
  {
    "success": true,
    "data": {
      "telegram": {
        "channelName": "TradeNov Index Options Elite Desk",
        "channelHandle": "@TradeNovIndexOptions",
        "inviteUrl": "https://t.me/TradeNovIndexOptions",
        "botUsername": "@tradenovbot",
        "botStartUrl": "https://t.me/tradenovbot"
      },
      "whatsapp": {
        "groupName": "TradeNov VIP Community",
        "inviteUrl": "https://chat.whatsapp.com/TradeNovVIPAlerts"
      }
    }
  }
  ```
