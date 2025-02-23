import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import InvoiceResolve from './route/invoice-routing-resolve.service';

const invoiceRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/invoice.component').then(m => m.InvoiceComponent),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/invoice-detail.component').then(m => m.InvoiceDetailComponent),
    resolve: {
      invoice: InvoiceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/invoice-update.component').then(m => m.InvoiceUpdateComponent),
    resolve: {
      invoice: InvoiceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/invoice-update.component').then(m => m.InvoiceUpdateComponent),
    resolve: {
      invoice: InvoiceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default invoiceRoute;
