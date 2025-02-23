import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITicketOrder, NewTicketOrder } from '../ticket-order.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITicketOrder for edit and NewTicketOrderFormGroupInput for create.
 */
type TicketOrderFormGroupInput = ITicketOrder | PartialWithRequiredKeyOf<NewTicketOrder>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITicketOrder | NewTicketOrder> = Omit<T, 'placedDate'> & {
  placedDate?: string | null;
};

type TicketOrderFormRawValue = FormValueOf<ITicketOrder>;

type NewTicketOrderFormRawValue = FormValueOf<NewTicketOrder>;

type TicketOrderFormDefaults = Pick<NewTicketOrder, 'id' | 'placedDate'>;

type TicketOrderFormGroupContent = {
  id: FormControl<TicketOrderFormRawValue['id'] | NewTicketOrder['id']>;
  placedDate: FormControl<TicketOrderFormRawValue['placedDate']>;
  status: FormControl<TicketOrderFormRawValue['status']>;
  code: FormControl<TicketOrderFormRawValue['code']>;
  invoiceId: FormControl<TicketOrderFormRawValue['invoiceId']>;
  customer: FormControl<TicketOrderFormRawValue['customer']>;
};

export type TicketOrderFormGroup = FormGroup<TicketOrderFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TicketOrderFormService {
  createTicketOrderFormGroup(ticketOrder: TicketOrderFormGroupInput = { id: null }): TicketOrderFormGroup {
    const ticketOrderRawValue = this.convertTicketOrderToTicketOrderRawValue({
      ...this.getFormDefaults(),
      ...ticketOrder,
    });
    return new FormGroup<TicketOrderFormGroupContent>({
      id: new FormControl(
        { value: ticketOrderRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      placedDate: new FormControl(ticketOrderRawValue.placedDate, {
        validators: [Validators.required],
      }),
      status: new FormControl(ticketOrderRawValue.status, {
        validators: [Validators.required],
      }),
      code: new FormControl(ticketOrderRawValue.code, {
        validators: [Validators.required],
      }),
      invoiceId: new FormControl(ticketOrderRawValue.invoiceId),
      customer: new FormControl(ticketOrderRawValue.customer, {
        validators: [Validators.required],
      }),
    });
  }

  getTicketOrder(form: TicketOrderFormGroup): ITicketOrder | NewTicketOrder {
    return this.convertTicketOrderRawValueToTicketOrder(form.getRawValue() as TicketOrderFormRawValue | NewTicketOrderFormRawValue);
  }

  resetForm(form: TicketOrderFormGroup, ticketOrder: TicketOrderFormGroupInput): void {
    const ticketOrderRawValue = this.convertTicketOrderToTicketOrderRawValue({ ...this.getFormDefaults(), ...ticketOrder });
    form.reset(
      {
        ...ticketOrderRawValue,
        id: { value: ticketOrderRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TicketOrderFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      placedDate: currentTime,
    };
  }

  private convertTicketOrderRawValueToTicketOrder(
    rawTicketOrder: TicketOrderFormRawValue | NewTicketOrderFormRawValue,
  ): ITicketOrder | NewTicketOrder {
    return {
      ...rawTicketOrder,
      placedDate: dayjs(rawTicketOrder.placedDate, DATE_TIME_FORMAT),
    };
  }

  private convertTicketOrderToTicketOrderRawValue(
    ticketOrder: ITicketOrder | (Partial<NewTicketOrder> & TicketOrderFormDefaults),
  ): TicketOrderFormRawValue | PartialWithRequiredKeyOf<NewTicketOrderFormRawValue> {
    return {
      ...ticketOrder,
      placedDate: ticketOrder.placedDate ? ticketOrder.placedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
