<script setup>
import { computed, onMounted, ref } from 'vue'
import { ArcElement, CategoryScale, Chart as ChartJS, DoughnutController, LineController, LineElement, LinearScale, PointElement, Tooltip } from 'chart.js'
import { Doughnut, Line } from 'vue-chartjs'
import DeleteModal from '../../components/DeleteModal.vue'
import { trains as managedTrains, deleteTrain, fetchTrains } from '../line/services/trainData'
import { adminStations, deleteStation, fetchAdminStations } from '../station/services/adminStationData'
import { api, apiError } from '../../services/backendApi'
import PageHeader from '../../components/PageHeader.vue'
import DashboardStatCard from '../../components/DashboardStatCard.vue'
import PageFooter from '../../components/PageFooter.vue'

ChartJS.register(ArcElement, CategoryScale, DoughnutController, LineController, LineElement, LinearScale, PointElement, Tooltip)

const summaryCards = ref([
  { label: '회원 수', value: '—', unit: '명', note: '' },
  { label: '공지 수', value: '—', unit: '건', note: '' },
  { label: 'QnA 답변 대기 수', value: '—', unit: '건', note: '' },
  { label: '교통서비스 수', value: '—', unit: '건', note: '' },
])

const serviceStatuses = ref([
  { name: '정상', count: 45, percent: '50.00%', color: 'green' },
  { name: '점검', count: 27, percent: '30.00%', color: 'yellow' },
  { name: '고장', count: 18, percent: '20.00%', color: 'red' },
])

const memberStatuses = ref([
  { name: '일반 유저', count: 90, percent: '75.0%', color: 'green' },
  { name: '임시 차단', count: 18, percent: '15.0%', color: 'yellow' },
  { name: '강퇴', count: 12, percent: '10.0%', color: 'red' },
])

const trains = computed(() => managedTrains.value.slice(0, 3).map(row => ({
  ...row, station: row.section, tone: row.status === '운행' ? 'running' : row.status === '점검' ? 'inspection' : 'broken',
})))
const stations = computed(() => adminStations.value.slice(0, 3).map(row => ({
  ...row, tone: row.status === '양호' ? 'running' : row.status === '점검 중' ? 'inspection' : 'broken',
})))
const deletion = ref(null)
const deletionComplete = ref(false)
const deletionError = ref('')
function requestDelete(kind, id) {
  deletionError.value = ''
  deletionComplete.value = false
  deletion.value = { kind, id }
}
function closeDelete() { deletion.value = null; deletionComplete.value = false }
async function confirmDelete() {
  if (deletionComplete.value) { closeDelete(); return }
  try {
    if (deletion.value.kind === 'train') await deleteTrain(deletion.value.id)
    else await deleteStation(deletion.value.id)
    deletionComplete.value = true
  } catch (error) {
    closeDelete()
    deletionError.value = apiError(error)
  }
}
const days = ['10.02', '10.03', '10.04', '10.05', '10.06', '10.07', '10.08']
const weeklyStatuses = ref({
  normal: [18, 34, 28, 46, 37, 29, 43],
  inspection: [6, 17, 11, 22, 14, 19, 8],
  broken: [13, 8, 16, 10, 5, 12, 17],
})
const statusColors = { green: '#67c89d', yellow: '#ffd15f', red: '#e87377' }
const memberColors = { green: '#67c89d', yellow: '#ffd15f', red: '#e87377' }
const serviceTotal = computed(() => serviceStatuses.value.reduce((sum, item) => sum + item.count, 0))
const memberTotal = computed(() => memberStatuses.value.reduce((sum, item) => sum + item.count, 0))
const servicePercent = count => serviceTotal.value ? `${((count / serviceTotal.value) * 100).toFixed(2)}%` : '0.00%'
const memberPercent = count => memberTotal.value ? `${((count / memberTotal.value) * 100).toFixed(1)}%` : '0.0%'
const percentageLabelsPlugin = {
  id: 'percentageLabels',
  afterDatasetsDraw(chart) {
    const values = chart.data.datasets[0]?.data || []
    const total = values.reduce((sum, value) => sum + Number(value || 0), 0)
    if (!total) return
    const context = chart.ctx
    const color = chart.options.plugins.percentageLabels?.color || '#ffffff'
    chart.getDatasetMeta(0).data.forEach((arc, index) => {
      const { x, y, startAngle, endAngle, innerRadius, outerRadius } = arc.getProps(['x', 'y', 'startAngle', 'endAngle', 'innerRadius', 'outerRadius'], true)
      const angle = (startAngle + endAngle) / 2
      const radius = (innerRadius + outerRadius) / 2
      const percent = `${Math.round((Number(values[index]) / total) * 100)}%`
      context.save()
      context.fillStyle = color
      context.font = '700 10px Arial, sans-serif'
      context.textAlign = 'center'
      context.textBaseline = 'middle'
      context.fillText(percent, x + Math.cos(angle) * radius, y + Math.sin(angle) * radius)
      context.restore()
    })
  },
}
const serviceChartData = computed(() => ({
  labels: [serviceStatuses.value[2], serviceStatuses.value[0], serviceStatuses.value[1]].map(item => item.name),
  datasets: [{
    data: [serviceStatuses.value[2], serviceStatuses.value[0], serviceStatuses.value[1]].map(item => item.count),
    backgroundColor: [serviceStatuses.value[2], serviceStatuses.value[0], serviceStatuses.value[1]].map(item => statusColors[item.color]),
    borderWidth: 0,
    hoverOffset: 3,
  }],
}))
const memberChartData = computed(() => ({
  labels: [...memberStatuses.value].map(item => item.name),
  datasets: [{
    data: memberStatuses.value.map(item => item.count),
    backgroundColor: memberStatuses.value.map(item => memberColors[item.color]),
    borderWidth: 0,
    hoverOffset: 3,
  }],
}))
const weeklyChartData = computed(() => ({
  labels: days,
  datasets: [
    { label: '정상', data: weeklyStatuses.value.normal, borderColor: '#27bd89', backgroundColor: '#27bd89', pointBackgroundColor: '#27bd89', pointRadius: 2, pointHoverRadius: 4, borderWidth: 2, tension: 0.15 },
    { label: '점검', data: weeklyStatuses.value.inspection, borderColor: '#ffae24', backgroundColor: '#ffae24', pointBackgroundColor: '#ffae24', pointRadius: 2, pointHoverRadius: 4, borderWidth: 2, tension: 0.15 },
    { label: '고장', data: weeklyStatuses.value.broken, borderColor: '#fa6c66', backgroundColor: '#fa6c66', pointBackgroundColor: '#fa6c66', pointRadius: 2, pointHoverRadius: 4, borderWidth: 2, tension: 0.15 },
  ],
}))
const doughnutOptions = cutout => ({
  responsive: true,
  maintainAspectRatio: false,
  cutout,
  rotation: -90,
  plugins: { legend: { display: false }, tooltip: { enabled: true }, percentageLabels: { color: '#ffffff' } },
})
const serviceChartOptions = doughnutOptions('40%')
const memberChartOptions = {
  ...doughnutOptions('54%'),
  plugins: { ...doughnutOptions('54%').plugins, percentageLabels: { color: '#ffffff' } },
}
const trendChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  interaction: { intersect: false, mode: 'index' },
  plugins: { legend: { display: true, position: 'top', align: 'end', labels: { usePointStyle: true, pointStyle: 'circle', boxWidth: 5, boxHeight: 5, padding: 10, color: '#8b9aab', font: { size: 8 } } }, tooltip: { enabled: true } },
  scales: {
    x: { grid: { display: false }, border: { display: false }, ticks: { color: '#8b9aab', font: { size: 8 }, maxRotation: 0 } },
    y: { min: 0, max: 50, ticks: { stepSize: 10, color: '#8b9aab', font: { size: 8 } }, grid: { color: '#edf1f3' }, border: { display: false } },
  },
}
onMounted(async () => {
  const results = await Promise.allSettled([
    api.members.list({ page: 1, size: 1 }),
    api.notices.list({ page: 1, size: 1 }),
    api.qna.adminList({ page: 1, size: 1, status: 'WAITING' }),
    fetchTrains({ page: 1, size: 3 }),
    fetchAdminStations({ page: 1, size: 3 }),
  ])
  summaryCards.value = summaryCards.value.map((card, index) => index < 3 && results[index].status === 'fulfilled' ? { ...card, value: String(results[index].value.totalCount ?? '—') } : card)
  for (const result of results.slice(3)) if (result.status === 'rejected') deletionError.value = apiError(result.reason)
})
</script>

<template>
  <div class="dashboard-shell" id="dashboard">
    <PageHeader />

    <main class="dashboard-main">
      <div class="page-heading">
        <h1>대시보드</h1>
        <time datetime="2026-10-07T15:52">2026.10.07(수) 15:52</time>
      </div>

      <section class="summary-grid" aria-label="주요 현황">
        <DashboardStatCard v-for="card in summaryCards" :key="card.label" v-bind="card" />
      </section>

      <section class="analytics-grid" aria-label="통계 현황">
        <article class="panel service-panel">
          <h2 class="panel-title">
            <svg class="chart-icon" viewBox="0 0 32 32" aria-hidden="true">
              <path d="M3 29.5h26M6 28V17h5v11M14 28V9h5v19M22 28V3h5v25" />
            </svg>
            교통서비스 상태 현황
          </h2>
          <div class="service-chart-content">
            <div class="donut donut--service" role="img" :aria-label="`교통서비스 전체 ${serviceTotal}건`">
              <Doughnut class="donut-chart-canvas" :data="serviceChartData" :options="serviceChartOptions" :plugins="[percentageLabelsPlugin]" />
              <div class="donut__center">
                <strong>전체</strong>
                <b>{{ serviceTotal }}건</b>
              </div>
            </div>
            <ul class="legend-list">
              <li v-for="item in serviceStatuses" :key="item.name">
                <i :class="`legend-dot legend-dot--${item.color}`"></i>
                <span>{{ item.name }}</span>
                <b>{{ item.count }}건 ({{ servicePercent(item.count) }})</b>
              </li>
            </ul>
          </div>
        </article>

        <article class="panel trend-panel">
          <h2 class="panel-title">
            <svg class="chart-icon" viewBox="0 0 32 32" aria-hidden="true">
              <path d="M3 29.5h26M6 28V17h5v11M14 28V9h5v19M22 28V3h5v25" />
            </svg>
            주간 교통서비스 상태 추이
          </h2>
          <div class="line-chart-wrap">
            <Line class="line-chart" :data="weeklyChartData" :options="trendChartOptions" aria-label="주간 교통서비스 상태 추이" />
          </div>
        </article>

        <article class="panel member-panel">
          <h2 class="panel-title">회원 현황</h2>
          <div class="member-chart-content">
            <div class="donut donut--member" role="img" :aria-label="`회원 현황 전체 ${memberTotal}명`">
              <Doughnut class="donut-chart-canvas" :data="memberChartData" :options="memberChartOptions" :plugins="[percentageLabelsPlugin]" />
              <div class="donut__center donut__center--small">
                <span>전체</span>
                <b>{{ memberTotal }}명</b>
              </div>
            </div>
            <ul class="member-legend">
              <li v-for="item in memberStatuses" :key="item.name">
                <i :class="`legend-dot legend-dot--${item.color}`"></i>
                <span>
                  {{ item.name }}
                  <b>{{ item.count }}명 <small>({{ memberPercent(item.count) }})</small></b>
                </span>
              </li>
            </ul>
          </div>
        </article>
      </section>

      <section class="management-grid" aria-label="열차와 역 관리">
        <article class="panel management-panel">
          <div class="management-heading">
            <h2 class="panel-title">열차 관리</h2>
            <div class="management-actions">
              <RouterLink class="button button--primary" to="/admin/line7/trains/new">등록</RouterLink>
              <RouterLink class="text-button" to="/admin/line7/trains">전체보기 ›</RouterLink>
            </div>
          </div>
          <div class="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>운행 상태</th>
                  <th>운행 구간</th>
                  <th>열차 번호</th>
                  <th>기관사</th>
                  <th>관리</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="train in trains" :key="train.id">
                  <td>
                    <span :class="`status-pill status-pill--${train.tone}`">
                      {{ train.status }}
                    </span>
                  </td>
                  <td>{{ train.station }}</td>
                  <td>{{ train.number }}</td>
                  <td>{{ train.operator }}</td>
                  <td class="row-actions">
                    <RouterLink class="button button--edit" :to="'/admin/line7/trains/' + train.id">수정</RouterLink>
                    <button class="button button--delete" type="button" @click="requestDelete('train', train.id)">삭제</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </article>

        <article class="panel management-panel">
          <div class="management-heading">
            <h2 class="panel-title">역 관리</h2>
            <div class="management-actions">
              <RouterLink class="button button--primary" to="/admin/line7/stations/new">등록</RouterLink>
              <RouterLink class="text-button" to="/admin/line7/stations">전체보기 ›</RouterLink>
            </div>
          </div>
          <div class="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>시설 상태</th>
                  <th>역 이름</th>
                  <th>역 번호</th>
                  <th>역무원</th>
                  <th>활성화 여부</th>
                  <th>관리</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="station in stations" :key="station.id">
                  <td>
                    <span :class="`status-pill status-pill--${station.tone}`">
                      {{ station.status }}
                    </span>
                  </td>
                  <td>{{ station.name }}</td>
                  <td>{{ station.number }}</td>
                  <td>{{ station.staff }}</td>
                  <td>{{ station.active ? '활성화' : '비활성화' }}</td>
                  <td class="row-actions">
                    <RouterLink class="button button--edit" :to="'/admin/line7/stations/' + station.id">수정</RouterLink>
                    <button class="button button--delete" type="button" @click="requestDelete('station', station.id)">삭제</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </article>
      </section>
    </main>

    <p v-if="deletionError" role="alert">{{ deletionError }}</p>
    <PageFooter />
    <DeleteModal v-if="deletion" :completed="deletionComplete" @confirm="confirmDelete" @close="closeDelete" />
  </div>
</template>







