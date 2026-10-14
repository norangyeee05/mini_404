<script setup>
import {computed,onMounted,ref} from 'vue';import api,{apiError} from '../../services/api';import PageHeader from '../../components/PageHeader.vue';import DashboardStatCard from '../../components/DashboardStatCard.vue';import PageFooter from '../../components/PageFooter.vue';import {useRouter} from 'vue-router'
const router=useRouter(),dashboard=ref(null),error=ref(''),loading=ref(true),days=ref([])
const summaryCards=computed(()=>{const d=dashboard.value||{},s=d.statistics||d.stats||d;return [{label:'회원 수',value:s.memberCount??s.totalMembers??'—',unit:'명',note:''},{label:'공지 수',value:s.noticeCount??s.totalNotices??'—',unit:'건',note:''},{label:'QnA 답변 대기 수',value:s.waitingQnaCount??s.pendingQnaCount??s.unansweredQnaCount??'—',unit:'건',note:''},{label:'교통서비스 수',value:s.serviceCount??s.totalServices??s.trainCount??'—',unit:'건',note:''}]})
const serviceStatuses=computed(()=>dashboard.value?.serviceStatuses||[]),memberStatuses=computed(()=>dashboard.value?.memberStatuses||[]),trains=computed(()=>dashboard.value?.recentTrains||[]),stations=computed(()=>dashboard.value?.recentStations||[])
onMounted(async()=>{try{const r=await api.get('/admin/dashboard');dashboard.value=r.data?.data??r.data;days.value=[]}catch(e){error.value=apiError(e)}finally{loading.value=false}})
function go(path){router.push(path)}
function totalOf(items){return items.reduce((sum,item)=>sum+Number(item.count||0),0)}
function statusLabel(value){return ({RUNNING:'운행 중',MAINTENANCE:'점검 중',BROKEN:'고장',INSPECTED:'점검 완료',NEEDS_INSPECTION:'점검 필요',IN_INSPECTION:'점검 중'})[value]||value}
function tone(value){return ({RUNNING:'running',MAINTENANCE:'inspection',BROKEN:'broken',INSPECTED:'running',NEEDS_INSPECTION:'broken',IN_INSPECTION:'inspection'})[value]||'running'}
</script>

<template>
  <div class="dashboard-shell" id="dashboard">
    <PageHeader />

    <main class="dashboard-main">
      <div class="page-heading">
        <h1>대시보드</h1>
        <time>{{ new Date().toLocaleString() }}</time>
      </div>

      <p v-if="error" role="alert">{{ error }}</p><p v-else-if="loading" role="status">현황을 불러오는 중입니다.</p><section class="summary-grid" aria-label="주요 현황">
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
            <div
              v-if="serviceStatuses.length"
              class="donut donut--service"
              role="img"
              :aria-label="`교통서비스 전체 ${totalOf(serviceStatuses)}건`"
            >
              <div class="donut__center">
                <strong>전체</strong>
                <b>{{ totalOf(serviceStatuses) }}건</b>
              </div>
              
            </div>
            <ul v-if="serviceStatuses.length" class="legend-list">
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
            <p v-if="!days.length">주간 추이 데이터가 제공되지 않았습니다.</p><svg v-else class="line-chart" viewBox="0 0 620 300" role="img" aria-label="주간 교통서비스 상태 추이">
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
              <polyline
                class="chart-line chart-line--green"
                points="55,104 143,104 231,99 319,94 407,81 495,92 583,64"
              />
              <polyline
                class="chart-line chart-line--yellow"
                points="55,213 143,206 231,201 319,214 407,219 495,210 583,205"
              />
              <polyline
                class="chart-line chart-line--red"
                points="55,229 143,224 231,220 319,231 407,235 495,229 583,224"
              />
              <g class="chart-points chart-points--green">
                <circle
                  v-for="(x, i) in [55,143,231,319,407,495,583]"
                  :key="x"
                  :cx="x"
                  :cy="[104,104,99,94,81,92,64][i]"
                  r="3"
                />
              </g>
              <g class="chart-points chart-points--yellow">
                <circle
                  v-for="(x, i) in [55,143,231,319,407,495,583]"
                  :key="x"
                  :cx="x"
                  :cy="[213,206,201,214,219,210,205][i]"
                  r="3"
                />
              </g>
              <g class="chart-points chart-points--red">
                <circle
                  v-for="(x, i) in [55,143,231,319,407,495,583]"
                  :key="x"
                  :cx="x"
                  :cy="[229,224,220,231,235,229,224][i]"
                  r="3"
                />
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
            <div
              v-if="memberStatuses.length"
              class="donut donut--member"
              role="img"
              :aria-label="`회원 현황 전체 ${totalOf(memberStatuses)}명`"
            >
              <div class="donut__center donut__center--small">
                <span>전체</span>
                <b>{{ totalOf(memberStatuses) }}명</b>
              </div>
              
            </div>
            <ul v-if="memberStatuses.length" class="member-legend">
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
              <button class="button button--primary" @click="go('/admin/line7/trains/new')">등록</button>
              <button class="text-button" @click="go('/admin/line7/trains')">전체보기 ›</button>
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
                <tr v-for="train in trains" :key="train.trainId">
                  <td>
                    <span :class="`status-pill status-pill--${tone(train.status)}`">
                      {{ statusLabel(train.status) }}
                    </span>
                  </td>
                  <td>{{ train.section }}</td>
                  <td>{{ train.trainNo }}</td>
                  <td>{{ train.driver }}</td>
                  <td class="row-actions">
                    <button class="button button--edit" @click="go(`/admin/line7/trains/${train.trainId}`)">수정</button>
                    <button class="button button--delete" @click="go(`/admin/line7/trains/${train.trainId}`)">비활성화</button>
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
              <button class="button button--primary" @click="go('/admin/line7/stations/new')">등록</button>
              <button class="text-button" @click="go('/admin/line7/stations')">전체보기 ›</button>
            </div>
          </div>
          <div class="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>역 이름</th>
                  <th>시설 상태</th>
                  <th>역무원</th>
                  <th>최근 점검일</th>
                  <th>점검 예정일</th>
                  <th>관리</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="station in stations" :key="station.stationId" :class="{ 'is-inactive': !station.active }">
                  <td>{{ station.stationName }}</td>
                  <td><span :class="`status-pill status-pill--${tone(station.facilityStatus)}`">{{ statusLabel(station.facilityStatus) }}</span></td>
                  <td>{{ station.staffName }}</td>
                  <td>{{ station.lastInspectionDate }}</td>
                  <td>{{ station.nextInspectionDueDate }}</td>
                  <td class="row-actions">
                    <button class="button button--edit" @click="go(`/admin/line7/stations/${station.stationId}`)">수정</button>
                    <button class="button button--delete" @click="go(`/admin/line7/stations/${station.stationId}`)">비활성화</button>
                  </td>
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





