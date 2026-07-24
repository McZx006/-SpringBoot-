<template>
  <section>
    <div class="section-header">
      <h1>学习资料</h1>
    </div>
    <div class="filter-bar">
      <el-input
        v-model="query.keyword"
        placeholder="搜索资料名称"
        clearable
        @keyup.enter="loadResources"
      >
        <template #prefix>
          <Search class="w-4 h-4" />
        </template>
      </el-input>
      <el-select v-model="query.typeId" clearable placeholder="资料类型">
        <el-option v-for="item in types" :key="item.id" :label="item.name" :value="item.id" />
      </el-select>
      <el-button type="primary" @click="loadResources">
        <Search class="w-4 h-4" />
        搜索
      </el-button>
      <el-button @click="reset">
        <RotateCcw class="w-4 h-4" />
        重置
      </el-button>
    </div>
    
    <div class="resource-grid" v-if="resources.length && !loading">
      <article
        class="resource-card"
        v-for="item in resources"
        :key="item.id"
        @click="$router.push(`/resources/${item.id}`)"
      >
        <div class="card-badges">
          <span v-if="item.isHot" class="badge hot">
            <Flame class="w-3 h-3" />
            热门
          </span>
          <span v-if="item.isNew" class="badge new">
            <Sparkles class="w-3 h-3" />
            最新
          </span>
          <span v-if="item.isRecommend" class="badge recommend">
            <Star class="w-3 h-3" />
            推荐
          </span>
        </div>
        <img class="cover" :src="resourceCover(item)" :alt="item.title" />
        <h3>{{ item.title }}</h3>
        <span class="type-name">{{ item.typeName || '未分类' }}</span>
        <p>{{ item.summary }}</p>
        <div class="meta-line">
          <span><Eye class="w-3 h-3" /> {{ item.viewCount || 0 }}</span>
          <span><Download class="w-3 h-3" /> {{ item.downloadCount || 0 }}</span>
          <span><Calendar class="w-3 h-3" /> {{ formatDate(item.addtime) }}</span>
        </div>
      </article>
    </div>
    
    <div v-else-if="loading" class="skeleton-grid">
      <div class="skeleton-card" v-for="i in 12" :key="i">
        <div class="skeleton cover-skeleton"></div>
        <div class="skeleton title-skeleton"></div>
        <div class="skeleton type-skeleton"></div>
        <div class="skeleton desc-skeleton"></div>
        <div class="skeleton meta-skeleton"></div>
      </div>
    </div>
    
    <div v-else class="empty-state">
      <div class="empty-icon">
        <BookOpen class="w-12 h-12" />
      </div>
      <h3>暂无学习资料</h3>
      <p>当前没有符合条件的学习资料，请尝试更换搜索条件</p>
    </div>
    
    <div v-if="pagination.total > pagination.limit" class="pagination-bar">
      <el-pagination
        v-model:current-page="query.page"
        :page-size="query.limit"
        :total="pagination.total"
        layout="total, prev, pager, next, jumper"
        @current-change="loadResources"
      />
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { Search, RotateCcw, Eye, Download, Calendar, Flame, Sparkles, Star, BookOpen } from '@lucide/vue'
import request from '../api/request'
import { resourceCover } from '../utils/resourceCover'

const query = reactive({ page: 1, limit: 12, keyword: '', typeId: '' })
const resources = ref([])
const types = ref([])
const loading = ref(false)
const pagination = reactive({ total: 0, page: 1, limit: 12 })

async function loadResources() {
  loading.value = true
  try {
    const res = await request.get('/resources/page', { params: query })
    resources.value = res.data.data.list
    pagination.total = res.data.data.total || 0
    pagination.page = res.data.data.page || 1
  } catch (error) {
    console.error('加载资料失败:', error)
    resources.value = []
  } finally {
    loading.value = false
  }
}

function reset() {
  query.keyword = ''
  query.typeId = ''
  query.page = 1
  loadResources()
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return dateStr.substring(0, 10)
}

onMounted(async () => {
  const typeRes = await request.get('/resource-types/list')
  types.value = typeRes.data.data
  await loadResources()
})
</script>

<style scoped>
.card-badges {
  position: absolute;
  top: var(--spacing-sm);
  left: var(--spacing-sm);
  display: flex;
  gap: 4px;
  z-index: 1;
}

.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.skeleton-card {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  background: var(--color-bg-card);
  border: 1px solid var(--color-gray-100);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
}

.skeleton.cover-skeleton {
  height: 140px;
  border-radius: var(--radius-md);
}

.skeleton.title-skeleton {
  height: 20px;
  width: 80%;
  border-radius: 4px;
}

.skeleton.type-skeleton {
  height: 20px;
  width: 40%;
  border-radius: 4px;
}

.skeleton.desc-skeleton {
  height: 16px;
  width: 100%;
  border-radius: 4px;
}

.skeleton.meta-skeleton {
  height: 16px;
  width: 60%;
  border-radius: 4px;
  margin-top: auto;
}

.empty-state {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}

.empty-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: rgba(37, 99, 235, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto var(--spacing-lg);
  color: var(--color-primary);
}

.empty-state h3 {
  margin: 0 0 var(--spacing-sm);
  font-size: var(--font-size-lg);
}

.empty-state p {
  margin: 0;
  color: var(--color-text-muted);
}

@media (max-width: 768px) {
  .skeleton-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .skeleton-grid {
    grid-template-columns: 1fr;
  }
}
</style>
