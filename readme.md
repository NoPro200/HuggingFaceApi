# Huggingface API

## Installation

### Current Version:  [![](https://jitpack.io/v/NoPro200/HuggingFaceApi.svg)](https://jitpack.io/#NoPro200/HuggingFaceApi)

<details>
 <summary>Maven</summary>

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
```xml
<dependency>
     <groupId>com.github.NoPro200</groupId>
     <artifactId>HuggingFaceApi</artifactId>
     <version>x.y.z</version>
</dependency>
```
</details>
<details>
<summary>Gradle</summary>

```
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
	    }
    }
```

```

dependencies {
		implementation 'com.github.NoPro200:HuggingFaceApi:x.y.z'
}

```
</details>

## Get an API Key

- Go to [Huggingface](https://huggingface.co)
- Create an Account
- Go to Account Settings and Create an [Access Token](https://huggingface.co/settings/tokens)
- Copy the Access Token

## Use the Library

### Text Generation

```java
public class Main{
    public static void Main(String[] args) {
        try{
            HuggingFaceText hf = new HuggingFaceText.Builder("Text Model", "API Key").build();

            HuggingFaceText.ChatOptions options = new HuggingFaceText.ChatOptions()
                            .setTemperature(0.5)
                            .setMaxTokens(1024)
                            .setTopP(0.7)
                            .setStream(true);

            List<Map<String, String>> messages = new ArrayList<>();
                    messages.add(Map.of("role", "system", "content", "System Prompt Here"));
                    messages.add(Map.of("role", "user", "content", "User Prompt Here"));

            String result = hf.chat(messages, options);

            System.out.println(result);
        } catch (Exeption e) {
            System.err.println(e.getMessage());
        }
    }
}

```

### Image Generation

```java
public class Main {
    public static void main(String[] args) {
        try {
            HuggingFaceImage hf = new HuggingFaceImage.Builder("Image Model", "API Key").build();
            byte[] image = hf.image("Prompt Here");
            
            String randomId = UUID.randomUUID().toString();
            String fileName = randomId + ".png";
            
            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                fos.write(image);
            }
            
            System.out.println("Bild erfolgreich gespeichert als: " + fileName);
            
        } catch (Exeption e) {
            System.err.println(e.getMessage());
        }
    }
}
```