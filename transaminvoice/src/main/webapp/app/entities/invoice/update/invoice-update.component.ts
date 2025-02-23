import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { InvoiceStatus } from 'app/entities/enumerations/invoice-status.model';
import { PaymentMethod } from 'app/entities/enumerations/payment-method.model';
import { InvoiceService } from '../service/invoice.service';
import { IInvoice } from '../invoice.model';
import { InvoiceFormGroup, InvoiceFormService } from './invoice-form.service';

@Component({
  selector: 'jhi-invoice-update',
  templateUrl: './invoice-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class InvoiceUpdateComponent implements OnInit {
  isSaving = false;
  invoice: IInvoice | null = null;
  invoiceStatusValues = Object.keys(InvoiceStatus);
  paymentMethodValues = Object.keys(PaymentMethod);

  usersSharedCollection: IUser[] = [];

  protected invoiceService = inject(InvoiceService);
  protected invoiceFormService = inject(InvoiceFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: InvoiceFormGroup = this.invoiceFormService.createInvoiceFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ invoice }) => {
      this.invoice = invoice;
      if (invoice) {
        this.updateForm(invoice);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const invoice = this.invoiceFormService.getInvoice(this.editForm);
    if (invoice.id !== null) {
      this.subscribeToSaveResponse(this.invoiceService.update(invoice));
    } else {
      this.subscribeToSaveResponse(this.invoiceService.create(invoice));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInvoice>>): void {
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

  protected updateForm(invoice: IInvoice): void {
    this.invoice = invoice;
    this.invoiceFormService.resetForm(this.editForm, invoice);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, invoice.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.invoice?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
