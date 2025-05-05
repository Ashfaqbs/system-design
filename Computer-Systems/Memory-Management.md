#  Memory Management

## **What is Memory Management?**

Memory management is the **process of efficiently allocating, using, and freeing RAM** (Random Access Memory) during a program’s execution. It decides:

* Where data lives (stack vs heap)
* How memory is accessed
* When memory is cleaned up (manual or automatic)

---

## Core Areas of Memory

### 1. **Stack**

* **What:** Fixed-size memory region used for **function calls** and **local variables**
* **Fast** access (LIFO – Last In First Out)
* Managed automatically
* Memory is freed when the function exits
* Limited in size

```java
void example() {
    int x = 5; // Lives in the stack
}
```

### 2. **Heap**

* **What:** Dynamic memory for objects and long-lived data
* **Slower** access than stack
* Needs manual or automatic memory management
* Lives beyond function scope

```java
String name = new String("Ashfaq"); // Lives in heap
```

---

## Stack vs Heap – Comparison

| Feature    | Stack                  | Heap                          |
| ---------- | ---------------------- | ----------------------------- |
| Allocation | Static (compile-time)  | Dynamic (runtime)             |
| Speed      | Very fast              | Slower                        |
| Lifetime   | Short (function scope) | Long (till freed or GC’d)     |
| Managed By | Compiler               | Developer / Garbage Collector |
| Size       | Small                  | Large                         |

---

## Memory Management Techniques

### 1. **Manual Memory Management**

* Developer explicitly allocates (`malloc`) and deallocates (`free`)
* Languages: **C, C++**
* Risk of:

  * Memory leaks (forgetting to `free`)
  * Dangling pointers (accessing freed memory)
  * Double free (freeing twice)

### 2. **Automatic / Garbage Collected**

* **Garbage Collector (GC)** tracks unused objects and reclaims memory.
* Languages: **Java, Python, Go**
* We don't manually free memory, but we **can still cause memory leaks** (e.g., unclosed resources, references in collections)

####  GC Algorithms (Java-style):

| Type            | Description                               |
| --------------- | ----------------------------------------- |
| Mark-and-Sweep  | Mark reachable objects, sweep unreachable |
| Generational GC | Young vs Old memory optimization          |
| Stop-the-World  | App is paused during collection (JVM)     |
| Concurrent GC   | GC runs alongside app (G1, ZGC)           |

---

##  Concepts Tied to Memory

### ➤ **Memory Leak**

* Memory that is allocated but never freed
* Even in garbage-collected languages (e.g., holding references in static fields or cache)

### ➤ **OutOfMemoryError**

* Happens when JVM or system heap is exhausted
* Often seen in high-load or misconfigured environments

### ➤ **Dangling Pointer**

* Refers to memory that was freed but still being accessed (common in C/C++)

### ➤ **Segmentation Fault**

* Happens when invalid memory is accessed (common in C)

---

## Tips to Optimize Memory Usage

###  In Java:

* Use primitives instead of wrappers when possible (`int` vs `Integer`)
* Avoid holding references in long-lived objects (like caches, static fields)
* Use object pooling for frequently used objects
* Choose the right GC (e.g., G1GC for low-latency apps)
* Close resources (streams, sockets, DB connections) using `try-with-resources`

###  In Python:

* Use built-in types (e.g., list, dict) wisely
* Be cautious with large objects in global scope
* Use `del` to delete references if needed
* Use tools like `gc`, `tracemalloc` for debugging

###  General:

* Profile memory with tools (e.g., VisualVM, JProfiler, Valgrind)
* Use memory pools or object reuse if allocation overhead is high
* Watch for reference cycles in Python or retained references in Java

---

##  Real-Life Examples

1. **Memory Leak in Java**
   A `Map<String, Object>` stores user sessions but never removes expired ones → JVM eventually runs out of memory.

2. **Dangling Pointer in C**
   Freeing a pointer, then using it again → undefined behavior or crash.

3. **Stack Overflow**
   Infinite recursion → stack memory is exhausted.

---

##  Summary

| Term                    | Meaning                                          |
| ----------------------- | ------------------------------------------------ |
| Stack                   | Fast, short-lived memory for functions           |
| Heap                    | Dynamic memory for objects                       |
| GC (Garbage Collection) | Auto cleanup of unused heap memory               |
| Memory Leak             | Memory not reclaimed due to lingering references |
| Dangling Pointer        | Access to freed memory (common in C/C++)         |
| OutOfMemoryError        | Not enough memory to continue execution          |

