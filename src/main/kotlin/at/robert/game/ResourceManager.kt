package at.robert.game

import com.badlogic.gdx.utils.Disposable

class ResourceManager : Disposable {
    private val resources = HashMap<String, Any>()

    fun <T : Any> load(name: String, loader: (String) -> T): T {
        val alreadyLoaded = resources[name]
        @Suppress("UNCHECKED_CAST") // we want an error here if this doesn't work
        alreadyLoaded as T?
        return if (alreadyLoaded == null) {
            val resource = loader(name)
            resources[name] = resource
            resource
        } else {
            alreadyLoaded
        }
    }

    override fun dispose() {
        resources.forEach { (_, resource) ->
            if (resource is Disposable) resource.dispose()
        }
        resources.clear()
    }


}
