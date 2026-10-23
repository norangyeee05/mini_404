import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/login/Login.vue'
import SignUp from '../views/login/SignUp.vue'
import Main from '../views/main/Main.vue'
import Resource from '../views/resource/Resource.vue'
import AdminResource from '../views/resource/AdminResource.vue'
import AdminMember from '../views/dashboard/AdminMember.vue'
import AdminDashboardView from '../views/dashboard/AdminDashboardView.vue'
import NoticeListView from '../views/notice/NoticeListView.vue'
import NoticeDetailView from '../views/notice/NoticeDetailView.vue'
import AdminNoticeListView from '../views/notice/AdminNoticeListView.vue'
import AdminNoticeCreateView from '../views/notice/AdminNoticeCreateView.vue'
import MemberQnaListView from '../views/qna/MemberQnaListView.vue'
import MemberQnaDetailView from '../views/qna/MemberQnaDetailView.vue'
import MemberQnaFormView from '../views/qna/MemberQnaFormView.vue'
import AdminQnaListView from '../views/qna/AdminQnaListView.vue'
import AdminQnaDetailView from '../views/qna/AdminQnaDetailView.vue'
import MyPage from '../views/mypage/MyPage.vue'
import RailLine from '../views/line/RailLine.vue'
import AdminRailLine from '../views/line/AdminRailLine.vue'
import Station from '../views/station/Station.vue'
import AdminStation from '../views/station/AdminStation.vue'
import Terms from '../views/login/Terms.vue'
import ErrorPage from '../views/error/Error.vue'
import { requireMember } from '../services/accessControl'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: Main },
    { path: '/notices', alias: '/notice', component: NoticeListView },
    { path: '/notice/detail', component: NoticeDetailView },
    { path: '/notices/:id', redirect: to => ({ path: '/notice/detail', query: { id: to.params.id } }) },
    { path: '/admin', redirect: '/admin/dashboard' },
    { path: '/admin/dashboard', name: 'dashboard', component: AdminDashboardView, meta: { requiresAdmin: true, requiresFullAdmin: true } },
    { path: '/admin/notices', component: AdminNoticeListView, meta: { requiresAdmin: true } },
    { path: '/admin/notices/detail', component: NoticeDetailView, props: { role: 'admin' }, meta: { requiresAdmin: true } },
    { path: '/notices/create', component: AdminNoticeCreateView, meta: { requiresAdmin: true } },
    { path: '/admin/notices/create', redirect: '/notices/create' },
    { path: '/admin/notices/new', redirect: '/notices/create' },
    { path: '/notices/new', redirect: '/notices/create' },
    { path: '/admin/notices/edit', component: AdminNoticeCreateView, props: { mode: 'edit' }, meta: { requiresAdmin: true } },
    { path: '/admin/qna', component: AdminQnaListView, meta: { requiresAdmin: true } },
    { path: '/admin/qna/pending', component: AdminQnaListView, props: { mode: 'pending' }, meta: { requiresAdmin: true } },
    { path: '/admin/qna/detail', component: AdminQnaDetailView, meta: { requiresAdmin: true } },
    { path: '/resources/new', name: 'admin-resource-new', component: AdminResource, meta: { requiresAdmin: true } },
    { path: '/admin/resources/new', redirect: '/resources/new' },
    { path: '/admin/resources/:resourceId/edit', name: 'admin-resource-edit', component: AdminResource, meta: { requiresAdmin: true } },
    { path: '/admin/resources/:resourceId?', name: 'admin-resources', component: AdminResource, meta: { requiresAdmin: true } },
    { path: '/resources/:resourceId?', component: Resource, meta: { title: '자료실', requiresMember: true } },
    { path: '/qna', component: MemberQnaListView, meta: { requiresMember: true } },
    { path: '/qna/create', component: MemberQnaFormView, meta: { requiresMember: true } },
    { path: '/qna/edit', component: MemberQnaFormView, props: { mode: 'edit' }, meta: { requiresMember: true } },
    { path: '/qna/detail', component: MemberQnaDetailView, meta: { requiresMember: true } },
    { path: '/qna/:id', redirect: to => ({ path: '/qna/detail', query: { id: to.params.id } }) },
    { path: '/admin/line7/stations/:stationId/detail', name: 'admin-station-detail', component: AdminStation, meta: { requiresAdmin: true } },
    { path: '/admin/line7/trains/:trainId/detail', name: 'admin-train-detail', component: AdminRailLine, meta: { requiresAdmin: true } },
    { path: '/admin/line7/stations', name: 'admin-stations', component: AdminStation, meta: { requiresAdmin: true } },
    { path: '/admin/line7/stations/new', name: 'admin-station-new', component: AdminStation, meta: { requiresAdmin: true, requiresFullAdmin: true } },
    { path: '/admin/line7/stations/:stationId', name: 'admin-station-edit', component: AdminStation, meta: { requiresAdmin: true, requiresFullAdmin: true } },
    { path: '/admin/line7/trains', name: 'admin-trains', component: AdminRailLine, meta: { requiresAdmin: true } },
    { path: '/admin/line7/trains/new', name: 'admin-train-new', component: AdminRailLine, meta: { requiresAdmin: true, requiresFullAdmin: true } },
    { path: '/admin/line7/trains/:trainId', name: 'admin-train-edit', component: AdminRailLine, meta: { requiresAdmin: true, requiresFullAdmin: true } },
    { path: '/line7/stations/:stationId', name: 'station', component: Station, meta: { title: '철도정보' } },
    { path: '/line7', name: 'line7', component: RailLine, meta: { title: '철도정보' } },
    { path: '/rail-info', redirect: to => ({ path: '/line7', query: to.query }) },
    { path: '/mypage', component: MyPage, meta: { requiresMember: true, requiresRegularMember: true } },
    { path: '/mypage/delete', component: MyPage, meta: { requiresMember: true, requiresRegularMember: true } },
    { path: '/members', redirect: '/admin/members' },
    { path: '/admin/members/:memberId?', component: AdminMember, meta: { requiresAdmin: true } },
    { path: '/admin/mypage', component: MyPage, meta: { requiresAdmin: true, requiresRegularMember: true } },
    { path: '/login', name: 'login', component: Login },
    { path: '/signup', name: 'signup', component: SignUp },
    { path: '/terms', name: 'terms', component: Terms },
    { path: '/error/:code(401|403|404|500)', name: 'error', component: ErrorPage },
    { path: '/:pathMatch(.*)*', redirect: '/error/404' },
  ],
})



router.beforeEach(async (to, from) => {
  if (!await requireMember(to)) return from.matched.length ? false : '/'
})
export default router




