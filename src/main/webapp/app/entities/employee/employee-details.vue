<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="employee">
        <h2 class="jh-entity-heading" data-cy="employeeDetailsHeading">
          <span v-text="t$('demoJhVue870App.employee.detail.title')"></span> {{ employee.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('demoJhVue870App.employee.firstName')"></span>
          </dt>
          <dd>
            <span>{{ employee.firstName }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.employee.lastName')"></span>
          </dt>
          <dd>
            <span>{{ employee.lastName }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.employee.email')"></span>
          </dt>
          <dd>
            <span>{{ employee.email }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.employee.phoneNumber')"></span>
          </dt>
          <dd>
            <span>{{ employee.phoneNumber }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.employee.hireDate')"></span>
          </dt>
          <dd>
            <span v-if="employee.hireDate">{{ formatDateLong(employee.hireDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.employee.salary')"></span>
          </dt>
          <dd>
            <span>{{ employee.salary }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.employee.commissionPct')"></span>
          </dt>
          <dd>
            <span>{{ employee.commissionPct }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.employee.level')"></span>
          </dt>
          <dd>
            <span>{{ employee.level }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.employee.contract')"></span>
          </dt>
          <dd>
            <span v-text="t$('demoJhVue870App.Contract.' + employee.contract)"></span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.employee.cv')"></span>
          </dt>
          <dd>
            <div v-if="employee.cv">
              <a @click="openFile(employee.cvContentType, employee.cv)" v-text="t$('entity.action.open')"></a>
              {{ employee.cvContentType }}, {{ byteSize(employee.cv) }}
            </div>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.employee.manager')"></span>
          </dt>
          <dd>
            <div v-if="employee.manager">
              <router-link :to="{ name: 'EmployeeView', params: { employeeId: employee.manager.id } }">{{
                employee.manager.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.employee.department')"></span>
          </dt>
          <dd>
            <div v-if="employee.department">
              <router-link :to="{ name: 'DepartmentView', params: { departmentId: employee.department.id } }">{{
                employee.department.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="employee.id" :to="{ name: 'EmployeeEdit', params: { employeeId: employee.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./employee-details.component.ts"></script>
