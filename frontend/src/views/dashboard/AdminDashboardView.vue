<script setup>
import { computed, onMounted, ref } from 'vue'
import PageHeader from '../../components/PageHeader.vue'
import DashboardStatCard from '../../components/DashboardStatCard.vue'
import PageFooter from '../../components/PageFooter.vue'
import { getAdminStations, getAdminTrains, getDashboard } from '../../api/admin'
import { errorMessage } from '../../api/client'

const data = ref(null)
const message = ref('')
const loading = ref(true)
const summaryCards = computed(() => data.value?.summaryCards || [])
const serviceStatuses = computed(() => data.value?.serviceStatuses || [])
const memberStatuses = computed(() => data.value?.memberStatuses || [])
const trains = computed(() => data.value?.trains || [])
const stations = computed(() => data.value?.stations || [])
const days = computed(() => data.value?.serviceTrend?.map((point) => point.date) || [])
const pageDate = computed(() => data.value?.generatedAt || '')
onMounted(async () => {
  const [dashboard, trainData, stationData] = await Promise.allSettled([getDashboard(), getAdminTrains(), getAdminStations()])
  if (dashboard.status === 'fulfilled') data.value = dashboard.value || {}
  else message.value = errorMessage(dashboard.reason, '대시보드를 불러오지 못했습니다.')
  if (trainData.status === 'fulfilled') data.value = { ...(data.value || {}), trains: Array.isArray(trainData.value) ? trainData.value : trainData.value?.items || [] }
  if (stationData.status === 'fulfilled') data.value = { ...(data.value || {}), stations: Array.isArray(stationData.value) ? stationData.value : stationData.value?.items || [] }
  loading.value = false
})
</script>

<template>
  <div class="dashboard-shell" id="dashboard">
    <PageHeader role="admin" />

    <main class="dashboard-main">
      <div class="page-heading">
        <h1>대시보드</h1>
        <time>{{ pageDate }}</time>
      </div>
      <p v-if="message" role="alert">{{ message }}</p>
      <p v-if="loading" role="status">불러오는 중...</p>

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
            <div v-if="serviceStatuses.length"
              class="donut donut--service"
              role="img"
              aria-label="교통서비스 정상 72.73%, 점검 22.73%, 고장 4.55%"
            >
              <div class="donut__center">
                <strong>전체</strong>
                <b>{{ data?.serviceTotal ?? '—' }}건</b>
              </div>
            </div>
            <ul class="legend-list">
              <li v-for="item in serviceStatuses" :key="item.name">
                <i :class="`legend-dot legend-dot--${item.color}`"></i>
                <span>{{ item.name }}</span>
                <b>{{ item.count }}건 ({{ item.percent }})</b>
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
            <svg class="line-chart" viewBox="0 0 620 300" role="img" aria-label="주간 교통서비스 정상, 점검, 고장 건수 추이">
              <g class="chart-grid-lines">
                <line
                  v-for="y in [40, 85, 130, 175, 220, 265]"
                  :key="y"
                  x1="42"
                  :y1="y"
                  x2="600"
                  :y2="y"
                />
              </g>
              <g class="chart-y-labels">
                <text v-for="(n, i) in [50, 40, 30, 20, 10, 0]" :key="n" x="25" :y="45 + i * 45">
                  {{ n }}
                </text>
              </g>
              <g class="chart-x-labels">
                <text v-for="(day, i) in days" :key="day" :x="55 + i * 88" y="286">
                  {{ day }}
                </text>
              </g>
              <g class="chart-legend">
                <circle cx="455" cy="18" r="4" class="chart-points--green" />
                <text x="463" y="21">정상</text>
                <circle cx="512" cy="18" r="4" class="chart-points--yellow" />
                <text x="520" y="21">점검</text>
                <circle cx="568" cy="18" r="4" class="chart-points--red" />
                <text x="576" y="21">고장</text>
              </g>
            </svg>
          </div>
        </article>

        <article class="panel member-panel">
          <h2 class="panel-title">회원 현황</h2>
          <div class="member-chart-content">
            <div v-if="memberStatuses.length"
              class="donut donut--member"
              role="img"
              aria-label="회원 일반 유저 74.1%, 임시 차단 15.6%, 강퇴 7.4%"
            >
              <div class="donut__center donut__center--small">
                <span>전체</span>
                <b>{{ data?.memberTotal ?? '—' }}명</b>
              </div>
            </div>
            <ul class="member-legend">
              <li v-for="item in memberStatuses" :key="item.name">
                <i :class="`legend-dot legend-dot--${item.color}`"></i>
                <span>
                  {{ item.name }}
                  <b>{{ item.count }}명 <small>({{ item.percent }})</small></b>
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
              <button class="button button--primary" disabled>등록</button>
              <button class="text-button" disabled>전체보기 ›</button>
            </div>
          </div>
          <div class="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>시설 상태</th>
                  <th>역 이름</th>
                  <th>열차 번호</th>
                  <th>역무원</th>
                  <th>관리</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="train in trains" :key="train.number">
                  <td>
                    <span :class="`status-pill status-pill--${train.tone}`">
                      {{ train.status }}
                    </span>
                  </td>
                  <td>{{ train.station }}</td>
                  <td>{{ train.number }}</td>
                  <td>{{ train.operator }}</td>
                  <td class="row-actions"><button class="button button--edit" disabled>수정</button><button class="button button--delete" disabled>비활성화</button></td>
                </tr>
              </tbody>
            </table>
          </div>
        </article>

        <article class="panel management-panel">
          <div class="management-heading">
            <h2 class="panel-title">역 관리</h2>
            <div class="management-actions">
              <button class="button button--primary" disabled>등록</button>
              <button class="text-button" disabled>전체보기 ›</button>
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
                  <th>상행/하행</th>
                  <th>관리</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="station in stations" :key="station.number">
                  <td>
                    <span :class="`status-pill status-pill--${station.tone}`">
                      {{ station.status }}
                    </span>
                  </td>
                  <td>{{ station.section }}</td>
                  <td>{{ station.number }}</td>
                  <td>{{ station.operator }}</td>
                  <td>
                    <span
                      :class="[
                        'status-pill',
                        station.tone === 'inspection'
                          ? 'status-pill--direction-down'
                          : 'status-pill--direction-up',
                      ]"
                    >
                      {{ station.direction }}
                    </span>
                  </td>
                  <td class="row-actions"><button class="button button--edit" disabled>수정</button><button class="button button--delete" disabled>비활성화</button></td>
                </tr>
              </tbody>
            </table>
          </div>
        </article>
      </section>
    </main>

    <PageFooter />
  </div>
</template>




