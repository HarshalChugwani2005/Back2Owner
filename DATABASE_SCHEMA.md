# Back2Owner Database Schema

## Overview
Back2Owner uses Firebase Firestore for real-time data storage. This document describes all collections, documents, and their structure.

---

## Collections

### 1. **Items** Collection
Stores information about lost and found items on campus.

**Collection Path:** `/items/{itemId}`

**Document Structure:**
```json
{
  "id": "string (UUID)",
  "title": "string (required)",
  "description": "string",
  "category": "string (e.g., electronics, documents, stationery, personal, clothing, other)",
  "photoURL": "string (Firebase Storage path)",
  "blurredPhotoURL": "string (blurred version for privacy)",
  "location": "string (campus location)",
  "status": "string (LOST, FOUND, CLAIMED, RESOLVED)",
  "itemType": "string (lost or found)",
  "timestamp": "number (Unix timestamp)",
  "reporterID": "string (Firebase UID)",
  "reporterName": "string",
  "reporterEmail": "string",
  "securityQuestion": "string (e.g., What color is the phone case?)",
  "securityAnswerHash": "string (SHA-256 hashed answer)",
  "claimedByID": "string (UID of claimer, optional)",
  "claimedAt": "number (timestamp of claim, optional)",
  "resolvedAt": "number (timestamp of resolution, optional)"
}
```

**Indexes:**
- Composite: `itemType` (Ascending), `timestamp` (Descending)
- Composite: `itemType` (Ascending), `category` (Ascending), `timestamp` (Descending)
- Composite: `status` (Ascending), `timestamp` (Descending)

---

### 2. **Users** Collection
Stores user profile information.

**Collection Path:** `/users/{uid}`

**Document Structure:**
```json
{
  "uid": "string (Firebase UID, required)",
  "email": "string (college email)",
  "displayName": "string",
  "profilePhotoURL": "string (optional)",
  "collegeID": "string (unique college identifier)",
  "createdAt": "number (Unix timestamp)",
  "itemsReported": "number (count of items reported)",
  "itemsFound": "number (count of items found/claimed)",
  "rating": "number (1.0 - 5.0, default 5.0)",
  "fcmTokens": "array of strings (for push notifications)",
  "isVerified": "boolean (email verified status)",
  "biography": "string (user bio, optional)"
}
```

**Indexes:**
- Single: `rating` (Descending)
- Single: `createdAt` (Descending)
- Single: `collegeID` (Ascending, for unique lookup)

---

### 3. **Claims** Collection
Stores claim requests made by users trying to claim found items.

**Collection Path:** `/claims/{claimId}`

**Document Structure:**
```json
{
  "id": "string (UUID)",
  "itemID": "string (reference to item)",
  "claimerID": "string (Firebase UID)",
  "claimerName": "string",
  "claimerEmail": "string",
  "securityAnswerHash": "string (SHA-256 hashed answer)",
  "message": "string (explanation of why they own the item)",
  "status": "string (pending, approved, rejected)",
  "createdAt": "number (Unix timestamp)",
  "respondedAt": "number (timestamp of response, optional)",
  "isVerified": "boolean (security answer verified)"
}
```

**Indexes:**
- Composite: `itemID` (Ascending), `status` (Ascending)
- Composite: `claimerID` (Ascending), `status` (Ascending)
- Single: `createdAt` (Descending)

---

### 4. **Notifications** Collection
Stores notifications for users (item matches, claim updates, etc.).

**Collection Path:** `/notifications/{notificationId}`

**Document Structure:**
```json
{
  "id": "string (UUID)",
  "userID": "string (Firebase UID)",
  "title": "string (notification title)",
  "message": "string (notification body)",
  "type": "string (item_match, claim_request, claim_approved, claim_rejected, etc.)",
  "relatedItemID": "string (optional, link to item)",
  "relatedClaimID": "string (optional, link to claim)",
  "timestamp": "number (Unix timestamp)",
  "isRead": "boolean (default false)"
}
```

**Indexes:**
- Composite: `userID` (Ascending), `timestamp` (Descending)
- Single: `isRead` (Ascending)

---

## Data Types Reference

| Type | Description |
|------|-------------|
| `string` | UTF-8 text |
| `number` | 64-bit double |
| `boolean` | true/false |
| `array` | Ordered list of values |
| `object` | Map/dictionary of key-value pairs |
| `timestamp` | Unix milliseconds since epoch |

---

## Security Rules Summary

### Read Access
- **Items:** Public read for non-DRAFT items
- **Users:** Each user can read their own profile
- **Claims:** Claimant and item reporter can read
- **Notifications:** Only recipient can read

### Write Access
- **Items:** Reporter can update/delete their items
- **Users:** Each user can update their own profile
- **Claims:** Claimant or reporter can update/delete
- **Notifications:** Cloud Functions only (no direct client writes)

---

## Cloud Storage Structure

### Photo Storage Paths
```
gs://back2owner-campus.appspot.com/items/{itemId}/photo.jpg
gs://back2owner-campus.appspot.com/items/{itemId}/blurred.jpg
gs://back2owner-campus.appspot.com/users/{userId}/profile.jpg
```

---

## Scalability Considerations

1. **Pagination:** Load items in batches of 50 per query
2. **Search:** Firestore doesn't support full-text search; use Algolia or implement client-side filtering
3. **Real-time Updates:** Use Firestore listeners for live feeds
4. **Archiving:** Implement TTL for resolved claims (e.g., 30 days)

---

## Backup & Recovery

- Enable Firestore automated backups in Firebase Console
- Store sensitive data (security answers) hashed only
- Implement data export for compliance requirements

