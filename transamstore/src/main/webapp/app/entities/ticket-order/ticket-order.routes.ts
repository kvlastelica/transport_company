import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import TicketOrderResolve from './route/ticket-order-routing-resolve.service';

const ticketOrderRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/ticket-order.component').then(m => m.TicketOrderComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/ticket-order-detail.component').then(m => m.TicketOrderDetailComponent),
    resolve: {
      ticketOrder: TicketOrderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/ticket-order-update.component').then(m => m.TicketOrderUpdateComponent),
    resolve: {
      ticketOrder: TicketOrderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/ticket-order-update.component').then(m => m.TicketOrderUpdateComponent),
    resolve: {
      ticketOrder: TicketOrderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default ticketOrderRoute;
