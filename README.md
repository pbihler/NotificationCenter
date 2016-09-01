# Notification Center for Java

This project provides a central event dispatch implementation, heavily inspired by [NSNotificationCenter of Cocoa](https://developer.apple.com/library/mac/documentation/Cocoa/Reference/Foundation/Classes/NSNotificationCenter_Class/).

## Simple usage Example

### Observer

```java
Observer o = new Observer(){
	public void receivedNotification(final Notification notification) {
		System.out.println("Received Notification: " + notification);
	}
}

/* Or with java 8:
 * Observer o = notification -> System.out.println("Received Notification: " + notification);
 */

...

DefaultNotificationCenter.instance().addObserver(o, Observee.MY_EVENT);

... (later)


DefaultNotificationCenter.instance().removeObserver(o);
```

### Observee

```java
String MY_EVENT = "MY_EVENT";

DefaultNotificationCenter.instance().postNotification(MY_EVENT);
```

## Remarks

In its default implementation, it is designed thread safe and lock free.

To use it directly in your project you have to include the [lombok.jar](https://projectlombok.org/download.html) into your classpath.

If you do not want to include [Project Lombok](https://projectlombok.org/) (and you considered this twice), run demlombok on the src folder and use the generated classes:

```bash
java -jar lombok.jar delombok src -d src-delomboked
```

## License

This project is MIT licensed