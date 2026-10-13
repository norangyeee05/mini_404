import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import AdminDashboardView from '../views/dashboard/AdminDashboardView.vue'
import GuestHomeView from '../views/home/GuestHomeView.vue'
import NoticeListView from '../views/notice/NoticeListView.vue'
import NoticeDetailView from '../views/notice/NoticeDetailView.vue'
import AdminNoticeListView from '../views/notice/AdminNoticeListView.vue'
import AdminNoticeCreateView from '../views/notice/AdminNoticeCreateView.vue'
import AdminResourceCreateView from '../views/archive/AdminResourceCreateView.vue'
import AdminResourceDetailView from '../views/archive/AdminResourceDetailView.vue'
import AdminResourceListView from '../views/archive/AdminResourceListView.vue'
import MemberResourceRequestView from '../views/archive/MemberResourceRequestView.vue'
import AdminMemberView from '../views/user/AdminMemberView.vue'
import MemberWithdrawView from '../views/user/MemberWithdrawView.vue'
import MemberProfileView from '../views/user/MemberProfileView.vue'
import SignupTermsView from '../views/user/SignupTermsView.vue'
import SignupCompleteView from '../views/user/SignupCompleteView.vue'
import SignupView from '../views/user/SignupView.vue'
import LoginView from '../views/user/LoginView.vue'
import AdminQnaListView from '../views/qna/AdminQnaListView.vue'
import AdminQnaDetailView from '../views/qna/AdminQnaDetailView.vue'
import MemberQnaListView from '../views/qna/MemberQnaListView.vue'
import MemberQnaDetailView from '../views/qna/MemberQnaDetailView.vue'
import MemberQnaFormView from '../views/qna/MemberQnaFormView.vue'
import UnauthorizedView from '../views/error/UnauthorizedView.vue'

const routes = [
  { path: '/terms', name: 'terms', component: SignupTermsView },
  { path: '/admin', name: 'admin-home', component: AdminDashboardView, meta: { roles: ['ADMIN'] } },
  { path: '/notices', name: 'notices', component: NoticeListView },
  { path: '/notices/:noticeId', name: 'notice-by-id', component: NoticeDetailView },
  { path: '/resources/request', name: 'resource-request', component: MemberResourceRequestView, meta: { roles: ['USER'] } },
  { path: '/resources/:resourceId', name: 'resource-by-id', component: AdminResourceDetailView, props: { role: 'member' } },
  { path: '/qna/my', name: 'member-qna-my', component: MemberQnaListView, meta: { roles: ['USER'] } },
  { path: '/qna/new', name: 'member-qna-new', component: MemberQnaFormView, props: { mode: 'create' }, meta: { roles: ['USER'] } },
  { path: '/qna/:qnaId/edit', name: 'member-qna-edit-by-id', component: MemberQnaFormView, props: { mode: 'edit' }, meta: { roles: ['USER'] } },
  { path: '/qna/:qnaId', name: 'member-qna-by-id', component: MemberQnaDetailView, meta: { roles: ['USER'] } },
  { path: '/admin/qna/:qnaId', name: 'admin-qna-by-id', component: AdminQnaDetailView, meta: { roles: ['ADMIN'] } },
  { path: '/admin/resources/new', name: 'admin-resource-new', component: AdminResourceCreateView, meta: { roles: ['ADMIN'] } },
  { path: '/admin/resources/:resourceId', name: 'admin-resource-by-id', component: AdminResourceDetailView, meta: { roles: ['ADMIN'] } },
  { path: '/admin/notices/new', name: 'admin-notice-new', component: AdminNoticeCreateView, meta: { roles: ['ADMIN'] } },
  { path: '/admin/notices/:noticeId', name: 'admin-notice-by-id', component: NoticeDetailView, props: { role: 'admin' }, meta: { roles: ['ADMIN'] } },
  {
    path: '/',
    name: 'guest-home',
    component: GuestHomeView,
  },
  {
    path: '/member/home',
    name: 'member-home',
    component: GuestHomeView,
    props: { role: 'member' },
    meta: { roles: ['USER'] },
  },
  {
    path: '/admin/dashboard',
    name: 'dashboard',
    component: AdminDashboardView,
    meta: { roles: ['ADMIN'] },
  },
  {
    path: '/error/401',
    name: 'unauthorized',
    component: UnauthorizedView,
  },
  {
    path: '/error/403',
    name: 'forbidden',
    component: UnauthorizedView,
    props: { code: '403' },
  },
  {
    path: '/error/404',
    name: 'not-found',
    component: UnauthorizedView,
    props: { code: '404' },
  },
  {
    path: '/error/500',
    name: 'server-error',
    component: UnauthorizedView,
    props: { code: '500' },
  },
  {
    path: '/notice',
    name: 'notice-list',
    component: NoticeListView,
  },
  {
    path: '/notice/detail',
    name: 'notice-detail',
    component: NoticeDetailView,
  },
  {
    path: '/admin/notices',
    name: 'admin-notice-list',
    component: AdminNoticeListView,
    meta: { roles: ['ADMIN'] },
  },
  {
    path: '/admin/notices/detail',
    name: 'admin-notice-detail',
    component: NoticeDetailView,
    props: { role: 'admin' },
    meta: { roles: ['ADMIN'] },
  },
  {
    path: '/admin/notices/create',
    name: 'admin-notice-create',
    component: AdminNoticeCreateView,
    meta: { roles: ['ADMIN'] },
  },
  {
    path: '/admin/notices/edit',
    name: 'admin-notice-edit',
    component: AdminNoticeCreateView,
    props: { mode: 'edit' },
    meta: { roles: ['ADMIN'] },
  },
  {
    path: '/admin/resources',
    name: 'admin-resource-list',
    component: AdminResourceListView,
    meta: { roles: ['ADMIN'] },
  },
  {
    path: '/admin/resources/create',
    name: 'admin-resource-create',
    component: AdminResourceCreateView,
    meta: { roles: ['ADMIN'] },
  },
  {
    path: '/admin/resources/detail',
    name: 'admin-resource-detail',
    component: AdminResourceDetailView,
    meta: { roles: ['ADMIN'] },
  },
  {
    path: '/resources/detail',
    name: 'member-resource-detail',
    component: AdminResourceDetailView,
    props: { role: 'member' },
  },
  {
    path: '/resources',
    name: 'member-resource-list',
    component: AdminResourceListView,
    props: { role: 'member' },
  },
  {
    path: '/admin/members',
    name: 'admin-members',
    component: AdminMemberView,
    meta: { roles: ['ADMIN'] },
  },
  {
    path: '/mypage',
    name: 'member-profile',
    component: MemberProfileView,
    meta: { roles: ['USER'] },
  },
  {
    path: '/mypage/withdraw',
    name: 'member-withdraw',
    component: MemberWithdrawView,
    meta: { roles: ['USER'] },
  },
  {
    path: '/signup/terms',
    name: 'signup-terms',
    component: SignupTermsView,
  },
  {
    path: '/signup',
    name: 'signup',
    component: SignupView,
  },
  {
    path: '/signup/complete',
    name: 'signup-complete',
    component: SignupCompleteView,
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
  },
  {
    path: '/admin/qna',
    name: 'admin-qna-list',
    component: AdminQnaListView,
    meta: { roles: ['ADMIN'] },
  },
  {
    path: '/admin/qna/pending',
    name: 'admin-qna-pending',
    component: AdminQnaListView,
    props: { mode: 'pending' },
    meta: { roles: ['ADMIN'] },
  },
  {
    path: '/admin/qna/detail',
    name: 'admin-qna-detail',
    component: AdminQnaDetailView,
    meta: { roles: ['ADMIN'] },
  },
  {
    path: '/qna',
    name: 'member-qna-list',
    component: MemberQnaListView,
    meta: { roles: ['USER'] },
  },
  {
    path: '/qna/detail',
    name: 'member-qna-detail',
    component: MemberQnaDetailView,
    meta: { roles: ['USER'] },
  },
  {
    path: '/qna/create',
    name: 'member-qna-create',
    component: MemberQnaFormView,
    props: { mode: 'create' },
    meta: { roles: ['USER'] },
  },
  {
    path: '/qna/edit',
    name: 'member-qna-edit',
    component: MemberQnaFormView,
    props: { mode: 'edit' },
    meta: { roles: ['USER'] },
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found-fallback',
    component: UnauthorizedView,
    props: { code: '404' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to) => {
  if (!to.meta.roles) return true
  const auth = useAuthStore()
  let user = auth.user
  try {
    if (!auth.loaded || !user) user = await auth.loadMe()
  } catch {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (!user) return { name: 'login', query: { redirect: to.fullPath } }
  if (!to.meta.roles.includes(user.role)) return { name: 'forbidden' }
  return true
})

export default router

