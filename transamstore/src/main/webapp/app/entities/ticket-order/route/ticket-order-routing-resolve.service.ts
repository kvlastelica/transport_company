import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITicketOrder } from '../ticket-order.model';
import { TicketOrderService } from '../service/ticket-order.service';

const ticketOrderResolve = (route: ActivatedRouteSnapshot): Observable<null | ITicketOrder> => {
  const id = route.params.id;
  if (id) {
    return inject(TicketOrderService)
      .find(id)
      .pipe(
        mergeMap((ticketOrder: HttpResponse<ITicketOrder>) => {
          if (ticketOrder.body) {
            return of(ticketOrder.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default ticketOrderResolve;
