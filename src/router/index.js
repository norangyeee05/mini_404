import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/login/Login.vue'
import SignUp from '../views/login/SignUp.vue'
import Main from '../views/main/Main.vue'
import Resource from '../views/resource/Resource.vue'
import ResourceRequest from '../views/resource/ResourceRequest.vue'
import AdminResourceRequests from '../views/resource/AdminResourceRequests.vue'
import AdminResource from '../views/resource/AdminResource.vue'
import AdminMember from '../views/dashboard/AdminMember.vue'
import AdminDashboardView from '../views/dashboard/AdminDashboardView.vue'
import AdminLine7View from '../views/dashboard/AdminLine7View.vue'
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
import ContentPage from '../views/ContentPage.vue'
import Terms from '../views/login/Terms.vue'
import ErrorPage from '../views/error/Error.vue'
import { requireMember } from '../services/accessControl'
import { currentMember } from '../services/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: Main },
    { path: '/notices', alias: '/notice', component: NoticeListView },
    { path: '/notice/detail', component: NoticeDetailView },
    { path: '/notices/:noticeId', component: NoticeDetailView, props: { role: 'guest' } },
    { path: '/admin', name: 'dashboard', component: AdminDashboardView, meta: { requiresAdmin: true } },
    { path: '/admin/dashboard', redirect: '/admin' },
    { path: '/admin/notices', component: AdminNoticeListView, meta: { requiresAdmin: true } },
    { path: '/admin/notices/detail', redirect: to => `/admin/notices/${to.query.id}` },
    { path: '/admin/notices/:noticeId', component: NoticeDetailView, props: { role: 'admin' }, meta: { requiresAdmin: true } },
    { path: '/admin/notices/new', component: AdminNoticeCreateView, meta: { requiresAdmin: true } },
    { path: '/admin/notices/:noticeId/edit', component: AdminNoticeCreateView, props: { mode: 'edit' }, meta: { requiresAdmin: true } },
    { path: '/notices/create', redirect: '/admin/notices/new' },
    { path: '/admin/notices/create', redirect: '/admin/notices/new' },
    { path: '/notices/new', redirect: '/admin/notices/new' },
    { path: '/admin/notices/edit', redirect: to => `/admin/notices/${to.query.id}/edit` },
    { path: '/admin/qna', component: AdminQnaListView, meta: { requiresAdmin: true } },
    { path: '/admin/qna/pending', component: AdminQnaListView, props: { mode: 'pending' }, meta: { requiresAdmin: true } },
    { path: '/admin/qna/detail', component: AdminQnaDetailView, meta: { requiresAdmin: true } },
    { path: '/admin/qna/:qnaId', component: AdminQnaDetailView, meta: { requiresAdmin: true } },
    { path: '/admin/resource-requests', component: AdminResourceRequests, meta: { requiresAdmin: true } },
    { path: '/admin/resource-requests/:requestId', component: AdminResourceRequests, meta: { requiresAdmin: true } },
    { path: '/resources/new', redirect: '/admin/resources/new' },
    { path: '/admin/resources/new', name: 'admin-resource-new', component: AdminResource, meta: { requiresAdmin: true } },
    { path: '/admin/resources/:resourceId/edit', name: 'admin-resource-edit', component: AdminResource, props: { mode: 'edit' }, meta: { requiresAdmin: true } },
    
    { path: '/admin/resources/:resourceId?', name: 'admin-resources', component: AdminResource, meta: { requiresAdmin: true } },
    { path: '/resources/request', component: ResourceRequest, meta: { requiresMember: true } },
    { path: '/resources/:resourceId?', component: Resource },
    { path: '/qna', component: MemberQnaListView, meta: { requiresMember: true } },
    { path: '/qna/new', component: MemberQnaFormView, meta: { requiresMember: true } },
    { path: '/qna/edit', redirect: to => `/qna/${to.query.id}/edit` },
    { path: '/qna/:qnaId/edit', component: MemberQnaFormView, props: { mode: 'edit' }, meta: { requiresMember: true } },
    { path: '/qna/my', component: MemberQnaListView, props: { mine: true }, meta: { requiresMember: true } },
    { path: '/qna/detail', component: MemberQnaDetailView, meta: { requiresMember: true } },
    { path: '/qna/:qnaId', component: MemberQnaDetailView, meta: { requiresMember: true } },
    { path: '/qna/create', redirect: '/qna/new' },
    { path: '/line7', component: ContentPage },
    { path: '/line7/stations/:stationId', component: ContentPage },
    { path: '/rail-info', component: ContentPage },
    { path: '/mypage', component: MyPage, meta: { requiresMember: true } },
    { path: '/mypage/withdraw', component: MyPage, meta: { requiresMember: true } },
    { path: '/mypage/delete', redirect: '/mypage/withdraw' },
    { path: '/members', redirect: '/admin/members' },
    { path: '/admin/line7/:type', component: AdminLine7View, meta: { requiresAdmin: true } },
    { path: '/admin/line7/:type/new', component: AdminLine7View, meta: { requiresAdmin: true } },
    { path: '/admin/line7/:type/:id', component: AdminLine7View, meta: { requiresAdmin: true } },
    { path: '/admin/members/:memberId?', component: AdminMember, meta: { requiresAdmin: true } },
    { path: '/login', name: 'login', component: Login },
    { path: '/signup', name: 'signup', component: SignUp },
    { path: '/terms', name: 'terms', component: Terms },
    { path: '/error/403', name: 'forbidden', component: ErrorPage },
    { path: '/error', name: 'error', component: ErrorPage },
    { path: '/error/:code(401|403|404|500)', component: ErrorPage },
    { path: '/:pathMatch(.*)*', redirect: '/error/404' },
  ],
})



router.beforeEach(async to => {
  if (!await requireMember(to)) return to.meta.requiresMember && !currentMember.value ? { path: '/login', query: { redirect: to.fullPath } } : '/error/403'
})
export default router
