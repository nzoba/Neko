package eu.kanade.tachiyomi

import android.app.Application
import androidx.core.content.ContextCompat
import eu.kanade.tachiyomi.data.cache.ChapterCache
import eu.kanade.tachiyomi.data.cache.CoverCache
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.data.track.TrackManager
import eu.kanade.tachiyomi.jobs.follows.FollowsSyncService
import eu.kanade.tachiyomi.jobs.migrate.V5MigrationService
import eu.kanade.tachiyomi.jobs.tracking.TrackingSyncService
import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.network.services.NetworkServices
import eu.kanade.tachiyomi.source.SourceManager
import eu.kanade.tachiyomi.source.online.MangaDexLoginHelper
import eu.kanade.tachiyomi.source.online.handlers.ApiMangaParser
import eu.kanade.tachiyomi.source.online.handlers.ArtworkHandler
import eu.kanade.tachiyomi.source.online.handlers.FollowsHandler
import eu.kanade.tachiyomi.source.online.handlers.ImageHandler
import eu.kanade.tachiyomi.source.online.handlers.LatestChapterHandler
import eu.kanade.tachiyomi.source.online.handlers.ListHandler
import eu.kanade.tachiyomi.source.online.handlers.MangaHandler
import eu.kanade.tachiyomi.source.online.handlers.PageHandler
import eu.kanade.tachiyomi.source.online.handlers.SearchHandler
import eu.kanade.tachiyomi.source.online.handlers.SimilarHandler
import eu.kanade.tachiyomi.source.online.handlers.StatusHandler
import eu.kanade.tachiyomi.source.online.handlers.external.AzukiHandler
import eu.kanade.tachiyomi.source.online.handlers.external.BilibiliHandler
import eu.kanade.tachiyomi.source.online.handlers.external.ComikeyHandler
import eu.kanade.tachiyomi.source.online.handlers.external.MangaHotHandler
import eu.kanade.tachiyomi.source.online.handlers.external.MangaPlusHandler
import eu.kanade.tachiyomi.ui.manga.MangaUpdateCoordinator
import eu.kanade.tachiyomi.ui.manga.TrackingCoordinator
import eu.kanade.tachiyomi.ui.similar.SimilarRepository
import eu.kanade.tachiyomi.ui.source.browse.BrowseRepository
import eu.kanade.tachiyomi.ui.source.latest.DisplayRepository
import eu.kanade.tachiyomi.util.chapter.ChapterFilter
import eu.kanade.tachiyomi.util.chapter.ChapterItemFilter
import eu.kanade.tachiyomi.util.manga.MangaMappings
import eu.kanade.tachiyomi.util.manga.MangaShortcutManager
import kotlinx.serialization.json.Json
import org.nekomanga.core.network.NetworkPreferences
import org.nekomanga.core.security.SecurityPreferences
import org.nekomanga.domain.library.LibraryPreferences
import org.nekomanga.domain.reader.ReaderPreferences
import org.nekomanga.domain.details.MangaDetailsPreferences
import tachiyomi.core.preference.AndroidPreferenceStore
import tachiyomi.core.preference.PreferenceStore
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingleton
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class AppModule(val app: Application) : InjektModule {

    override fun InjektRegistrar.registerInjectables() {
        addSingleton(app)

        addSingletonFactory { DatabaseHelper(app) }

        addSingletonFactory { ChapterCache(app) }

        addSingletonFactory { CoverCache(app) }

        addSingletonFactory { NetworkHelper(app) }

        addSingletonFactory { NetworkServices() }

        addSingletonFactory { SourceManager() }

        addSingletonFactory { DownloadManager(app) }

        addSingletonFactory { TrackManager(app) }

        addSingletonFactory { ChapterFilter() }

        addSingletonFactory { ChapterItemFilter() }

        addSingletonFactory {
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        }

        addSingletonFactory { MangaMappings(app.applicationContext) }

        addSingleton(FollowsHandler())

        addSingleton(ArtworkHandler())

        addSingleton(MangaHandler())

        addSingleton(ApiMangaParser())

        addSingleton(SearchHandler())

        addSingleton(ListHandler())

        addSingleton(PageHandler())

        addSingleton(ImageHandler())

        addSingleton(SimilarHandler())

        addSingleton(LatestChapterHandler())

        addSingleton(MangaDexLoginHelper())

        addSingleton(MangaPlusHandler())

        addSingleton(BilibiliHandler())

        addSingleton(AzukiHandler())

        addSingleton(ComikeyHandler())

        addSingleton(MangaHotHandler())

        addSingleton(StatusHandler())

        addSingleton(FollowsSyncService())

        addSingleton(V5MigrationService())

        addSingleton(TrackingSyncService())

        addSingleton(SimilarRepository())

        addSingleton(MangaUpdateCoordinator())

        addSingleton(TrackingCoordinator())

        addSingleton(DisplayRepository())

        addSingleton(BrowseRepository())

        addSingletonFactory { Json { ignoreUnknownKeys = true } }

        addSingletonFactory { ChapterFilter() }

        addSingletonFactory { MangaShortcutManager() }

        // Asynchronously init expensive components for a faster cold start
        ContextCompat.getMainExecutor(app).execute {

            get<NetworkHelper>()

            get<SourceManager>()

            get<DatabaseHelper>()

            get<DownloadManager>()
        }
    }
}

class PreferenceModule(val application: Application) : InjektModule {
    override fun InjektRegistrar.registerInjectables() {
        addSingletonFactory<PreferenceStore> {
            AndroidPreferenceStore(application)
        }

        addSingletonFactory {
            SecurityPreferences(get())
        }

        addSingletonFactory {
            LibraryPreferences(get())
        }

        addSingletonFactory {
            ReaderPreferences(get())
        }

        addSingletonFactory {
            NetworkPreferences(get(), BuildConfig.DEBUG)
        }

        addSingletonFactory {
            MangaDetailsPreferences(get())
        }

        addSingletonFactory {
            PreferencesHelper(
                context = application,
                preferenceStore = get(),
            )
        }
    }
}
