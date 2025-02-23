import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITicketOrder, NewTicketOrder } from '../ticket-order.model';

export type PartialUpdateTicketOrder = Partial<ITicketOrder> & Pick<ITicketOrder, 'id'>;

type RestOf<T extends ITicketOrder | NewTicketOrder> = Omit<T, 'placedDate'> & {
  placedDate?: string | null;
};

export type RestTicketOrder = RestOf<ITicketOrder>;

export type NewRestTicketOrder = RestOf<NewTicketOrder>;

export type PartialUpdateRestTicketOrder = RestOf<PartialUpdateTicketOrder>;

export type EntityResponseType = HttpResponse<ITicketOrder>;
export type EntityArrayResponseType = HttpResponse<ITicketOrder[]>;

@Injectable({ providedIn: 'root' })
export class TicketOrderService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/ticket-orders');

  create(ticketOrder: NewTicketOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(ticketOrder);
    return this.http
      .post<RestTicketOrder>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(ticketOrder: ITicketOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(ticketOrder);
    return this.http
      .put<RestTicketOrder>(`${this.resourceUrl}/${this.getTicketOrderIdentifier(ticketOrder)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(ticketOrder: PartialUpdateTicketOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(ticketOrder);
    return this.http
      .patch<RestTicketOrder>(`${this.resourceUrl}/${this.getTicketOrderIdentifier(ticketOrder)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTicketOrder>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTicketOrder[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTicketOrderIdentifier(ticketOrder: Pick<ITicketOrder, 'id'>): number {
    return ticketOrder.id;
  }

  compareTicketOrder(o1: Pick<ITicketOrder, 'id'> | null, o2: Pick<ITicketOrder, 'id'> | null): boolean {
    return o1 && o2 ? this.getTicketOrderIdentifier(o1) === this.getTicketOrderIdentifier(o2) : o1 === o2;
  }

  addTicketOrderToCollectionIfMissing<Type extends Pick<ITicketOrder, 'id'>>(
    ticketOrderCollection: Type[],
    ...ticketOrdersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const ticketOrders: Type[] = ticketOrdersToCheck.filter(isPresent);
    if (ticketOrders.length > 0) {
      const ticketOrderCollectionIdentifiers = ticketOrderCollection.map(ticketOrderItem => this.getTicketOrderIdentifier(ticketOrderItem));
      const ticketOrdersToAdd = ticketOrders.filter(ticketOrderItem => {
        const ticketOrderIdentifier = this.getTicketOrderIdentifier(ticketOrderItem);
        if (ticketOrderCollectionIdentifiers.includes(ticketOrderIdentifier)) {
          return false;
        }
        ticketOrderCollectionIdentifiers.push(ticketOrderIdentifier);
        return true;
      });
      return [...ticketOrdersToAdd, ...ticketOrderCollection];
    }
    return ticketOrderCollection;
  }

  protected convertDateFromClient<T extends ITicketOrder | NewTicketOrder | PartialUpdateTicketOrder>(ticketOrder: T): RestOf<T> {
    return {
      ...ticketOrder,
      placedDate: ticketOrder.placedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTicketOrder: RestTicketOrder): ITicketOrder {
    return {
      ...restTicketOrder,
      placedDate: restTicketOrder.placedDate ? dayjs(restTicketOrder.placedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTicketOrder>): HttpResponse<ITicketOrder> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTicketOrder[]>): HttpResponse<ITicketOrder[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
