import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'transamstoreApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'customer',
    data: { pageTitle: 'transamstoreApp.customer.home.title' },
    loadChildren: () => import('./customer/customer.routes'),
  },
  {
    path: 'ticket',
    data: { pageTitle: 'transamstoreApp.ticket.home.title' },
    loadChildren: () => import('./ticket/ticket.routes'),
  },
  {
    path: 'ticket-order',
    data: { pageTitle: 'transamstoreApp.ticketOrder.home.title' },
    loadChildren: () => import('./ticket-order/ticket-order.routes'),
  },
  {
    path: 'order-item',
    data: { pageTitle: 'transamstoreApp.orderItem.home.title' },
    loadChildren: () => import('./order-item/order-item.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
