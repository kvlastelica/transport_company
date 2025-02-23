import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITicket } from 'app/entities/ticket/ticket.model';
import { TicketService } from 'app/entities/ticket/service/ticket.service';
import { ITicketOrder } from 'app/entities/ticket-order/ticket-order.model';
import { TicketOrderService } from 'app/entities/ticket-order/service/ticket-order.service';
import { OrderItemService } from '../service/order-item.service';
import { IOrderItem } from '../order-item.model';
import { OrderItemFormGroup, OrderItemFormService } from './order-item-form.service';

@Component({
  selector: 'jhi-order-item-update',
  templateUrl: './order-item-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class OrderItemUpdateComponent implements OnInit {
  isSaving = false;
  orderItem: IOrderItem | null = null;

  ticketsSharedCollection: ITicket[] = [];
  ticketOrdersSharedCollection: ITicketOrder[] = [];

  protected orderItemService = inject(OrderItemService);
  protected orderItemFormService = inject(OrderItemFormService);
  protected ticketService = inject(TicketService);
  protected ticketOrderService = inject(TicketOrderService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: OrderItemFormGroup = this.orderItemFormService.createOrderItemFormGroup();

  compareTicket = (o1: ITicket | null, o2: ITicket | null): boolean => this.ticketService.compareTicket(o1, o2);

  compareTicketOrder = (o1: ITicketOrder | null, o2: ITicketOrder | null): boolean => this.ticketOrderService.compareTicketOrder(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orderItem }) => {
      this.orderItem = orderItem;
      if (orderItem) {
        this.updateForm(orderItem);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const orderItem = this.orderItemFormService.getOrderItem(this.editForm);
    if (orderItem.id !== null) {
      this.subscribeToSaveResponse(this.orderItemService.update(orderItem));
    } else {
      this.subscribeToSaveResponse(this.orderItemService.create(orderItem));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrderItem>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(orderItem: IOrderItem): void {
    this.orderItem = orderItem;
    this.orderItemFormService.resetForm(this.editForm, orderItem);

    this.ticketsSharedCollection = this.ticketService.addTicketToCollectionIfMissing<ITicket>(
      this.ticketsSharedCollection,
      orderItem.ticket,
    );
    this.ticketOrdersSharedCollection = this.ticketOrderService.addTicketOrderToCollectionIfMissing<ITicketOrder>(
      this.ticketOrdersSharedCollection,
      orderItem.order,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.ticketService
      .query()
      .pipe(map((res: HttpResponse<ITicket[]>) => res.body ?? []))
      .pipe(map((tickets: ITicket[]) => this.ticketService.addTicketToCollectionIfMissing<ITicket>(tickets, this.orderItem?.ticket)))
      .subscribe((tickets: ITicket[]) => (this.ticketsSharedCollection = tickets));

    this.ticketOrderService
      .query()
      .pipe(map((res: HttpResponse<ITicketOrder[]>) => res.body ?? []))
      .pipe(
        map((ticketOrders: ITicketOrder[]) =>
          this.ticketOrderService.addTicketOrderToCollectionIfMissing<ITicketOrder>(ticketOrders, this.orderItem?.order),
        ),
      )
      .subscribe((ticketOrders: ITicketOrder[]) => (this.ticketOrdersSharedCollection = ticketOrders));
  }
}
