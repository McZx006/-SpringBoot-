<template>
  <section>
    <h1>在线考试</h1>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索试卷名称" />
      <div></div>
      <el-button type="primary" @click="loadPapers">搜索</el-button>
      <el-button @click="reset">重置</el-button>
    </div>
    <el-table :data="papers">
      <el-table-column prop="name" label="试卷名称" />
      <el-table-column prop="description" label="试卷说明" />
      <el-table-column prop="duration" label="考试时长">
        <template #default="{ row }">{{ row.duration }} 分钟</template>
      </el-table-column>
      <el-table-column prop="totalScore" label="总分" />
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button type="primary" @click="startExam(row.id)">开始考试</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/request'

const router = useRouter()
const papers = ref([])
const keyword = ref('')

function startExam(id) {
  router.push(`/exam/${id}`)
}

async function loadPapers() {
  const res = await request.get('/exampapers/page', { params: { page: 1, limit: 10, keyword: keyword.value } })
  papers.value = res.data.data.list
}

function reset() {
  keyword.value = ''
  loadPapers()
}

onMounted(async () => {
  await loadPapers()
})
</script>
