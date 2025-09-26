
# OllamaController
## generate

**URL:** `localhost:8080/art`

**Type:** `GET`


**Content-Type:** `application/x-www-form-urlencoded`

**Description:** 



**Query-parameters:**

| Parameter | Type | Required | Description | Since | Example |
|-----------|------|----------|-------------|-------|---------|
|message|string|true|No comments found.|-|In which country Art is valued most in th world? Just give me answer in one sentence.|


**Request-example:**
```bash
curl -X GET -i 'localhost:8080/art/?message=In which country Art is valued most in th world? Just give me answer in one sentence.'
```

**Response-example:**
```json
[
  "",
  ""
]
```

## stream

**URL:** `localhost:8080/art/joke/stream`

**Type:** `GET`


**Content-Type:** `application/x-www-form-urlencoded`

**Description:** 



**Query-parameters:**

| Parameter | Type | Required | Description | Since | Example |
|-----------|------|----------|-------------|-------|---------|
|message|string|true|No comments found.|-|Tell me an Art related joke|


**Request-example:**
```bash
curl -X GET -i 'localhost:8080/art/joke/stream?message=Tell me an Art related joke'
```

**Response-example:**
```json
[
  "",
  ""
]
```

## streamJson

**URL:** `localhost:8080/art/joke/stream/json`

**Type:** `GET`


**Content-Type:** `application/x-www-form-urlencoded`

**Description:** 



**Query-parameters:**

| Parameter | Type | Required | Description | Since | Example |
|-----------|------|----------|-------------|-------|---------|
|message|string|true|No comments found.|-|Tell me an Art related joke|


**Request-example:**
```bash
curl -X GET -i 'localhost:8080/art/joke/stream/json?message=Tell me an Art related joke'
```

**Response-example:**
```json
[
  "",
  ""
]
```

## inquire

**URL:** `localhost:8080/art/{user}/assistant`

**Type:** `GET`


**Content-Type:** `application/x-www-form-urlencoded`

**Description:** 


**Path-parameters:**

| Parameter | Type | Required | Description | Since | Example |
|-----------|------|----------|-------------|-------|---------|
|user|string|true|No comments found.|-||

**Query-parameters:**

| Parameter | Type | Required | Description | Since | Example |
|-----------|------|----------|-------------|-------|---------|
|question|string|true|No comments found.|-||


**Request-example:**
```bash
curl -X GET -i 'localhost:8080/art/{user}/assistant?question='
```

**Response-example:**
```json
[
  "",
  ""
]
```

