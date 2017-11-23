# scorpius
[![License](https://img.shields.io/github/license/dungba88/scorpius.svg?maxAge=2592000)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/org.dungba/joo-scorpius.svg?maxAge=2592000)](http://mvnrepository.com/artifact/org.dungba/joo-scorpius)

Scorpius is an asynchronous framework for defining and executing trigger in Java. Trigger is a type of event-driven programming, which you define an independent handler and register it with an event. When that event is fired, the associated trigger will be executed. A trigger can have an optional condition, to specify whether the trigger should be executed for a specific event payload.

**continous integration**

[![Build Status](https://travis-ci.org/dungba88/scorpius.svg?branch=master)](https://travis-ci.org/dungba88/scorpius)
[![Coverage Status](https://coveralls.io/repos/github/dungba88/scorpius/badge.svg?branch=master)](https://coveralls.io/github/dungba88/scorpius?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/26ea58b6bf0f47149f376feb311d679b)](https://www.codacy.com/app/dungba88/scorpius?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dungba88/scorpius&amp;utm_campaign=Badge_Grade)

## trigger in scorpius

A trigger in Scorpius is a Java class which implements `Trigger` interface and has one single method:

```java
void execute(TriggerExecutionContext executionContext) throws TriggerExecutionException;
```

The trigger is the action/handler in even-driven programming, it will be executed one its associated event is raised. It does not care who call it, it only cares about the event, or the input data passed to it. The `executionContext` parameter will contain everything a trigger needed to perform its action. And when the trigger finishes it job, it calls `executionContext.finish(result)` so that the one who raise the event will get the result.

Before a trigger can be used, it needs to be registered with `TriggerManager`, along with an *event* and/or an optional *condition*. When an event is raised, the associated condition will be checked against and if matches, the associated trigger will be executed.

## benefit of triggers

Because triggers are event-driven, so you will have a loosely coupled code, where each trigger can be developed, registered and executed independently without affect other components. Besides, there are couple of benefits:

- Integrating is easy, just raise the event with correct payload.
- Testing is easy, just construct a payload and use it to raise the event.
- Logging is easy, just log the payload, result and any possible exception occurred while executing the trigger.

If you have played with *Amazon Lambda*, then this will be more or less the same.

## install

Scorpius can be installed easily with Maven:

```
<dependency>
    <groupId>org.dungba</groupId>
    <artifactId>joo-libra</artifactId>
    <version>1.1.1</version>
</dependency>
```

## how to use

1. Creating a trigger

Creating a trigger is easy, just extend the `AbstractTrigger` and you will be good to go.

```java
public class SampleTrigger extends AbstractTrigger<SampleRequest, BaseResponse> {

    @Override
    public void execute(TriggerExecutionContext executionContext) throws TriggerExecutionException {
        // get the request from executionContext
        SampleRequest theRequest = (SampleRequest) executionContext.getRequest();
        
	// do some works here
        // ...
        
	// and return the response
        executionContext.finish(response));
    }
}
```

Because Java is strongly typed, a trigger needs to define its input (a class which extends `BaseRequest`) and output (a class which extends `BaseResponse`). The input is important because it is used by `TriggerManager` when trying to decode a JSON string request into the correct input of the trigger.

2. Register it with `TriggerManager`

Register the trigger with event "greet"

```java
triggerManager.registerTrigger("greet").withAction(SampleTrigger::new);
```

Register the trigger with event "greet" and a condition

```java
triggerManager.registerTrigger("greet")
              .withCondition(SampleCondition::new)
              .withAction(SampleTrigger::new);
```

Register the trigger to be executed periodically

```java
// the following code will register SampleTrigger to be executed
// every 1000ms, with an initial delay of 1000ms
triggerManager.registerPeriodicEvent(new PeriodicTaskMessage(1000, 1000, new SampleRequest()))
              .withAction(SampleTrigger::new);
```

3. Fire the event

Fire the event you registered on step 2, so that the associated trigger will be executed. Examples:

Fire the event with a request and ignore the result

```java
triggerManager.fireEvent("greet", new SampleRequest());
```

Fire the event with a request and handle the result

```java
triggerManager.fireEvent("greet", new SampleRequest())
              .done(response -> // handle the response)
              .fail(ex -> // handle the failure);
```

The above example uses a concept called `Promise`. The `fireEvent` doesn't actually return the trigger result, since it needs to be asynchronous. Instead, it will return a `Promise`, which is assured to return something in the future. Your code can register the `done` and `fail` callback to handle the result or failure respectively.

But `Promise` is not free, it comes with a price: extra computation, spinlock, Atomic instructions and all. So there is another way to register the callback:

```java
triggerManager.fireEvent("greet", new SampleRequest(),
                          response -> // handle the response,
                          ex -> // handle the failure);
```
This is almost identical to the previous example, except that you register the callback directly inside the `fireEvent` call. This will use a `SimplePromise` which doesn't needs all those extra cost you have on previous example. Of course, if you need to handle the result only after finishing some works, then you will need to stick to the real `Promise`, like this:

```java
Promise<BaseResponse, TriggerExecutionException> promise = 
    triggerManager.fireEvent("greet", new SampleRequest());

// do some works
//...

// handle the result
promise.done(response -> // handle the response)
       .fail(ex -> // handle the failure);
```

## trigger execution flow

Put it altogether, the flow when you raising an event is as below:

- A client `A` raise the event with a payload
- `TriggerManager` accept the event, create an `executionContext` associated with it, the pass to a `TriggerHandlingStrategy`. The `executionContext` will contain the event, payload and the trigger itself. Also `TriggerManager` will return a `Promise` to the client `A`
- `TriggerHandlingStrategy` will execute the trigger, immediately or sometimes in the future.
- The trigger will perform its duty, and call `finish` when it is done, or `fail` when it encounters an error and cannot finish its job.
- Client `A` promise callback will be called, and handle the result/exception from the trigger.

The `TriggerHandlingStrategy` is a concept to decouple the `TriggerManager` and the execution of the triggers, more will be covered on section [#extend](#extend)

## extend

Almost everything in Scorpius is extensible, or configurable. The most prominient one is the `TriggerHandlingStrategy`.

`TriggerHandlingStrategy` is how you want a trigger to be executed when its associated event is raised. There are several builtin implementation:

- `DefaultHandlingStrategy`: This is the default, it will execute the triggers immediately, in the same `Thread` as the caller
- `DisruptorHandlingStrategy`: This will use LMAX Disruptor to execute the triggers. It is the fastest and most efficient strategy besides the default one
- `ExecutorHandlingStrategy`: This will use Java `ExecutorService` to execute the triggers. You can specify number of threads used by the `ExecutorService`
- `QueueHandlingStrategy`: This will an adhoc queue to execute the trigger

*Which strategy to be used?*

Well, it depends on the situation. With the default one, the caller will be blocked until the trigger fulfills its job or fails with an exception. So this strategy will be faster and more favorable if all of your triggers doesn't block (i.e they are asynchronous) and their executions is very fast.

If you want your trigger to prematurely return result to caller, and continue doing it job independently, then `DisruptorHandlingStrategy` is more favorable. You will have more throughputs at the cost of slightly increased latency.
