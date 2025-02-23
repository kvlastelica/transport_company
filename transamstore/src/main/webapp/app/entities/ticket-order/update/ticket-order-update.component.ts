import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { OrderStatus } from 'app/entities/enumerations/order-status.model';
import { TicketOrderService } from '../service/ticket-order.service';
import { ITicketOrder } from '../ticket-order.model';
import { TicketOrderFormGroup, TicketOrderFormService } from './ticket-order-form.service';

@Component({
  selector: 'jhi-ticket-order-update',
  templateUrl: './ticket-order-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TicketOrderUpdateComponent implements OnInit {
  isSaving = false;
  ticketOrder: ITicketOrder | null = null;
  orderStatusValues = Object.keys(OrderStatus);

  customersSharedCollection: ICustomer[] = [];

  protected ticketOrderService = inject(TicketOrderService);
  protected ticketOrderFormService = inject(TicketOrderFormService);
  protected customerService = inject(CustomerService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TicketOrderFormGroup = this.ticketOrderFormService.createTicketOrderFormGroup();

  compareCustomer = (o1: ICustomer | null, o2: ICustomer | null): boolean => this.customerService.compareCustomer(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ticketOrder }) => {
      this.ticketOrder = ticketOrder;
      if (ticketOrder) {
        this.updateForm(ticketOrder);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ticketOrder = this.ticketOrderFormService.getTicketOrder(this.editForm);
    if (ticketOrder.id !== null) {
      this.subscribeToSaveResponse(this.ticketOrderService.update(ticketOrder));
    } else {
      this.subscribeToSaveResponse(this.ticketOrderService.create(ticketOrder));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITicketOrder>>): void {
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

  protected updateForm(ticketOrder: ITicketOrder): void {
    this.ticketOrder = ticketOrder;
    this.ticketOrderFormService.resetForm(this.editForm, ticketOrder);

    this.customersSharedCollection = this.customerService.addCustomerToCollectionIfMissing<ICustomer>(
      this.customersSharedCollection,
      ticketOrder.customer,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.customerService
      .query()
      .pipe(map((res: HttpResponse<ICustomer[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomer[]) =>
          this.customerService.addCustomerToCollectionIfMissing<ICustomer>(customers, this.ticketOrder?.customer),
        ),
      )
      .subscribe((customers: ICustomer[]) => (this.customersSharedCollection = customers));
  }
}
