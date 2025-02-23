import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITicket, NewTicket } from '../ticket.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITicket for edit and NewTicketFormGroupInput for create.
 */
type TicketFormGroupInput = ITicket | PartialWithRequiredKeyOf<NewTicket>;

type TicketFormDefaults = Pick<NewTicket, 'id'>;

type TicketFormGroupContent = {
  id: FormControl<ITicket['id'] | NewTicket['id']>;
  name: FormControl<ITicket['name']>;
  description: FormControl<ITicket['description']>;
  price: FormControl<ITicket['price']>;
  productSize: FormControl<ITicket['productSize']>;
  image: FormControl<ITicket['image']>;
  imageContentType: FormControl<ITicket['imageContentType']>;
};

export type TicketFormGroup = FormGroup<TicketFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TicketFormService {
  createTicketFormGroup(ticket: TicketFormGroupInput = { id: null }): TicketFormGroup {
    const ticketRawValue = {
      ...this.getFormDefaults(),
      ...ticket,
    };
    return new FormGroup<TicketFormGroupContent>({
      id: new FormControl(
        { value: ticketRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(ticketRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(ticketRawValue.description),
      price: new FormControl(ticketRawValue.price, {
        validators: [Validators.required, Validators.min(0)],
      }),
      productSize: new FormControl(ticketRawValue.productSize, {
        validators: [Validators.required],
      }),
      image: new FormControl(ticketRawValue.image),
      imageContentType: new FormControl(ticketRawValue.imageContentType),
    });
  }

  getTicket(form: TicketFormGroup): ITicket | NewTicket {
    return form.getRawValue() as ITicket | NewTicket;
  }

  resetForm(form: TicketFormGroup, ticket: TicketFormGroupInput): void {
    const ticketRawValue = { ...this.getFormDefaults(), ...ticket };
    form.reset(
      {
        ...ticketRawValue,
        id: { value: ticketRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TicketFormDefaults {
    return {
      id: null,
    };
  }
}
