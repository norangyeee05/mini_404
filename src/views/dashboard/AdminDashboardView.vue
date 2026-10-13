<script setup>
import PageHeader from '../../components/PageHeader.vue'
import DashboardStatCard from '../../components/DashboardStatCard.vue'
import PageFooter from '../../components/PageFooter.vue'

const summaryCards = [
  { label: '회원 수', value: '131', unit: '명', note: '어제 대비 +12명' },
  { label: '공지 수', value: '27', unit: '건', note: '어제 대비 +2명' },
  { label: 'QnA 답변 대기 수', value: '5', unit: '건', note: '어제 대비 -3건' },
  { label: '교통서비스 수', value: '110', unit: '건', note: '전체 서비스 중 정상 65건' },
]

const serviceStatuses = [
  { name: '정상', count: 80, percent: '72.73%', color: 'green' },
  { name: '점검', count: 25, percent: '22.73%', color: 'yellow' },
  { name: '고장', count: 5, percent: '4.55%', color: 'red' },
]

const memberStatuses = [
  { name: '일반 유저', count: 100, percent: '74.1%', color: 'green' },
  { name: '임시 차단', count: 21, percent: '15.6%', color: 'yellow' },
  { name: '강퇴', count: 10, percent: '7.4%', color: 'red' },
]

const trains = [
  { status: '운행', station: '석남 ~ 까치울', number: '1000', operator: '김철수', tone: 'running' },
  { status: '점검', station: '까치울 ~ 석남', number: '1001', operator: '이영희', tone: 'inspection' },
  { status: '고장', station: '석남 ~ 까치울', number: '1002', operator: '박민준', tone: 'broken' },
]

const stations = [
  { status: '운행', section: '석남 ~ 까치울', number: '1000', operator: '김철수', direction: '상행', tone: 'running' },
  { status: '점검', section: '까치울 ~ 석남', number: '1001', operator: '이영희', direction: '하행', tone: 'inspection' },
  { status: '고장', section: '석남 ~ 까치울', number: '1002', operator: '박민준', direction: '상행', tone: 'broken' },
]

const days = ['10.02', '10.03', '10.04', '10.05', '10.06', '10.07', '10.08']
</script>

<template>
  <div class="dashboard-shell" id="dashboard">
    <PageHeader role="admin" />

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
            <div
              class="donut donut--service"
              role="img"
              aria-label="교통서비스 정상 72.73%, 점검 22.73%, 고장 4.55%"
            >
              <div class="donut__center">
                <strong>전체</strong>
                <b>110건</b>
              </div>
              <span class="donut-label donut-label--top">5%</span>
              <span class="donut-label donut-label--left">23%</span>
              <span class="donut-label donut-label--bottom">73%</span>
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
              class="donut donut--member"
              role="img"
              aria-label="회원 일반 유저 74.1%, 임시 차단 15.6%, 강퇴 7.4%"
            >
              <div class="donut__center donut__center--small">
                <span>전체</span>
                <b>131명</b>
              </div>
              <span class="member-donut-label">74.1%</span>
              <span class="member-donut-label member-donut-label--top">7.4%</span>
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
              <button class="button button--primary">등록</button>
              <button class="text-button">전체보기 ›</button>
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
                  <td class="row-actions">
                    <button class="button button--edit">수정</button>
                    <button class="button button--delete">삭제</button>
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
              <button class="button button--primary">등록</button>
              <button class="text-button">전체보기 ›</button>
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
                  <td class="row-actions">
                    <button class="button button--edit">수정</button>
                    <button class="button button--delete">삭제</button>
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




