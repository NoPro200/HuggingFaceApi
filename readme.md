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
- The Access Token musst be "READ"
- Copy the Access Token

## Use the Library

### Text Generation

```java
public class Main{
    public static void Main(String[] args) {
        try{
            HuggingFaceText hf = new HuggingFaceText.Builder("Text Model Example: mistralai/Mistral-7B-Instruct-v0.3 is a good option", "API Key").build();

            HuggingFaceText.ChatOptions options = new HuggingFaceText.ChatOptions()
                            .setTemperature(0.5) # how serious or how funny
                            .setMaxTokens(1024) # maximum length of the answer
                            .setTopP(0.7)
                            .setStream(true); # whether the messages are sent in fractions or all at once (fractions is slightly faster, but the response is still complete at the end)

            List<Map<String, String>> messages = new ArrayList<>();
                    messages.add(Map.of("role", "system", "content", "System Prompt Here")); # Description of what the bot is like. For example: You are a bot on my Discord server that helps people with programming questions.
                    messages.add(Map.of("role", "user", "content", "User Prompt Here")); # The user's prompt. For example: generate an example Python script.

            String result = hf.chat(messages, options); # The result of the request

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
            HuggingFaceImage hf = new HuggingFaceImage.Builder("Image Model Example: XLabs-AI/flux-RealismLora is a good option", "API Key").build();
            byte[] image = hf.image("Prompt Here"); # what should the picture look like?
            
            String randomId = UUID.randomUUID().toString();
            String fileName = randomId + ".png";
            
            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                fos.write(image);
            }
            
            System.out.println("Image saved as " + fileName);
            
        } catch (Exeption e) {
            System.err.println(e.getMessage());
        }
    }
}
```
