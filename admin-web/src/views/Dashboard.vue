<template>
  <div>
    <h1>控制台</h1>
    <div class="metric-grid">
      <div class="metric-card">学员总数 <strong>{{ metrics.students }}</strong></div>
      <div class="metric-card">资料总数 <strong>{{ metrics.resources }}</strong></div>
      <div class="metric-card">试卷总数 <strong>{{ metrics.papers }}</strong></div>
      <div class="metric-card">待回复留言 <strong>0</strong></div>
    </div>
  </div>
</template>

<script>
import request from '../api/request'

export default {
  data() {
    return {
      metrics: { students: 0, resources: 0, papers: 0 }
    }
  },
  async created() {
    const [students, resources, papers] = await Promise.all([
      request.get('/xueyuan/page', { params: { page: 1, limit: 1 } }),
      request.get('/resources/page', { params: { page: 1, limit: 1 } }),
      request.get('/exampapers/page', { params: { page: 1, limit: 1 } })
    ])
    this.metrics.students = students.data.data.total
    this.metrics.resources = resources.data.data.total
    this.metrics.papers = papers.data.data.total
  }
}
</script>
