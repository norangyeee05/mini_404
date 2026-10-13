import { createRouter, createWebHistory } from 'vue-router'
import AdminDashboardView from '../views/dashboard/AdminDashboardView.vue'
import NoticeListView from '../views/notice/NoticeListView.vue'
import NoticeDetailView from '../views/notice/NoticeDetailView.vue'
import AdminNoticeListView from '../views/notice/AdminNoticeListView.vue'
import AdminNoticeCreateView from '../views/notice/AdminNoticeCreateView.vue'
import AdminMemberView from '../views/user/AdminMemberView.vue'
import AdminQnaListView from '../views/qna/AdminQnaListView.vue'
import AdminQnaDetailView from '../views/qna/AdminQnaDetailView.vue'
import MemberQnaListView from '../views/qna/MemberQnaListView.vue'
import MemberQnaDetailView from '../views/qna/MemberQnaDetailView.vue'
import MemberQnaFormView from '../views/qna/MemberQnaFormView.vue'

const routes = [
  {
    path: '/',
    name: 'dashboard',
    component: AdminDashboardView,
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
  },
  {
    path: '/admin/notices/detail',
    name: 'admin-notice-detail',
    component: NoticeDetailView,
    props: { role: 'admin' },
  },
  {
    path: '/admin/notices/create',
    name: 'admin-notice-create',
    component: AdminNoticeCreateView,
  },
  {
    path: '/admin/notices/edit',
    name: 'admin-notice-edit',
    component: AdminNoticeCreateView,
    props: { mode: 'edit' },
  },
  {
    path: '/admin/members',
    name: 'admin-members',
    component: AdminMemberView,
  },
  {
    path: '/admin/qna',
    name: 'admin-qna-list',
    component: AdminQnaListView,
  },
  {
    path: '/admin/qna/pending',
    name: 'admin-qna-pending',
    component: AdminQnaListView,
    props: { mode: 'pending' },
  },
  {
    path: '/admin/qna/detail',
    name: 'admin-qna-detail',
    component: AdminQnaDetailView,
  },
  {
    path: '/qna',
    name: 'member-qna-list',
    component: MemberQnaListView,
  },
  {
    path: '/qna/detail',
    name: 'member-qna-detail',
    component: MemberQnaDetailView,
  },
  {
    path: '/qna/create',
    name: 'member-qna-create',
    component: MemberQnaFormView,
    props: { mode: 'create' },
  },
  {
    path: '/qna/edit',
    name: 'member-qna-edit',
    component: MemberQnaFormView,
    props: { mode: 'edit' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router

