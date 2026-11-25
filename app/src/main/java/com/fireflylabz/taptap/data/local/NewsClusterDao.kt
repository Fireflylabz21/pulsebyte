package com.fireflylabz.taptap.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsClusterDao {
    @Query("SELECT * FROM news_clusters ORDER BY createdAt DESC")
    suspend fun getAllClustersList(): List<NewsClusterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClusters(clusters: List<NewsClusterEntity>)

    @Query("DELETE FROM news_clusters")
    suspend fun clearAll()
}
