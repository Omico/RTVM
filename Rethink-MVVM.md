# Rethink MVVM (Draft)

Note: The Chinese version requires verification, supplementation, and review, while the English version is awaiting translation. If you have any inquiries, please don't hesitate to open an issue.

![https://creativecommons.org/licenses/by-nc-sa/4.0](https://img.shields.io/badge/docs%20license-CC%20BY--NC--SA%204.0-green?logo=creativecommons)

RTVM 是一种基于 MVVM 设计思路的改良型 Android MVVM 架构。目的是为了进一步消灭 MVVM 造成的模板代码。得益于 [Kotlin](https://kotlinlang.org) 的语言特性，以及 [KSP (Kotlin Symbol Processing)](https://github.com/google/ksp) 的支持，RTVM 得以在此基础上诞生。

## 为什么要使用 RTVM

MVVM 是一种非常优秀的架构，但是它也有一些缺点：

1. 由于 MVVM 的特性，我们需要为每一个 ViewModel 编写一个对应的 Model，这样会造成大量的模板代码。
2. 随着业务逻辑的增加，我们需要同时修改 ViewModel 和 Model，来对新的业务进行适配。

得益于 Kotlin 的语言特性，RTVM 得以通过使用 KSP 生成样板代码，来解决上述问题。

## RTVM 是如何实现的

以下是一个非常简单的 MVVM 案例：

```kotlin
data class UserViewState(
    val avatar: String? = null,
    val name: String = "",
) {
    companion object {
        val Initial = UserViewState()
    }
}

class UserViewModel : ViewModel() {
    private val avatar: MutableStateFlow<String?> = MutableStateFlow(null)
    private val user: MutableStateFlow<String> = MutableStateFlow("")

    val state: StateFlow<UserViewState> = combine(avatar, user, ::UserViewState)
        .stateIn(viewModelScope, SharingStarted.Lazily, UserViewState.Initial)
}
```

在上述代码中，我们可以清晰的发现，这些代码都是样板代码，且我们还不得不写。于是这就体现出代码生成的必要性，但奇怪的是我并没有听到周围有人提出过这个问题。于是我就想，为什么不自己写一个呢？为此 RTVM 就诞生了。

RTVM 的实现思路非常简单，就是通过 KSP 生成样板代码。在 RTVM 中，我们只需要为 `UserViewState` 加入 `@RtvmState` 注解，完整示例如下：

```kotlin
@RtvmState(scope = "user")
data class UserViewState(
    val avatar: String? = null,
    val name: String = "",
) {
    companion object {
        val Initial = UserViewState()
    }
}
```

然后 RTVM 就会自动生成 `UserViewStateDispatcher`，生成的内容如下：

```kotlin
class UserViewStateDispatcher {
    private val avatar: MutableStateFlow<String?> = MutableStateFlow(null)

    private val user: MutableStateFlow<String> = MutableStateFlow("")

    val flow: Flow<UserViewState> = combine(avatar, user, ::UserViewState)

    private val parameters: Parameters = Parameters(avatar, user)

    operator fun invoke(block: Parameters.() -> Unit): Unit = block(parameters)

    class Parameters(
        val countDown: MutableStateFlow<Int>,
        val pin: MutableStateFlow<String?>,
        val timer: MutableStateFlow<Int>,
    )
}
```

在下面的案例中，我们将会借助于 Kotlin 的[扩展函数](https://kotlinlang.org/docs/extensions.html#extension-functions)和[运算符重载](https://kotlinlang.org/docs/operator-overloading.html)，通过使用 `stateDispatcher(block: Parameters.() -> Unit)` 来为 RTVM 生成的 `UserViewStateDispatcher` 更新其状态。

```kotlin
class UserViewModel : ViewModel(), Stateful<UserViewState> {
    private val stateDispatcher = UserViewStateDispatcher()

    override val state: StateFlow<UserViewState> =
        stateDispatcher.flow.stateIn(viewModelScope, SharingStarted.Lazily, UserViewState.Initial)

    fun updateAvatar(avatar: String?) {
        dispatcher {
            this.avatar.value = avatar
        }
    }

    fun updateName(name: String) {
        dispatcher {
            this.name.value = name
        }
    }
}
```

以上只是简单的状态更新案例，更多的请等待后续文档的更新，或参见[示例](example)。
